/*
 * Copyright 2016 Oyabun AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.oyabun.proctor.proxy.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import se.oyabun.proctor.ProctorServerConfiguration;
import se.oyabun.proctor.events.handler.ProxyHandlerMatchedEvent;
import se.oyabun.proctor.events.handler.ProxyHandlerNotMatchedEvent;
import se.oyabun.proctor.events.http.ProxyReplySentEvent;
import se.oyabun.proctor.events.http.ProxyRequestReceivedEvent;
import se.oyabun.proctor.exceptions.InputNotMatchedException;
import se.oyabun.proctor.exceptions.NoHandleForNameException;
import se.oyabun.proctor.handler.ProctorRouteHandler;
import se.oyabun.proctor.handler.manager.ProctorRouteHandlerManager;
import se.oyabun.proctor.handler.properties.ProctorHandlerConfiguration;
import se.oyabun.proctor.http.HttpRequestData;
import se.oyabun.proctor.http.HttpResponseData;
import se.oyabun.proctor.http.client.ProctorHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static io.netty.buffer.Unpooled.copiedBuffer;

/**
 * Proctor Netty Http Responder
 */
@Component
public class ProctorHttpResponder {

    private static final Logger log = LoggerFactory.getLogger(ProctorHttpResponder.class);

    private final ApplicationEventPublisher applicationEventPublisher;
    private final ProctorRouteHandlerManager proctorRouteHandlerManager;
    private final ProctorHttpClient proctorHttpClient;
    private final ProctorServerConfiguration localServerConfiguration;

    @Autowired
    public ProctorHttpResponder(final ApplicationEventPublisher applicationEventPublisher,
                                final ProctorRouteHandlerManager proctorRouteHandlerManager,
                                final ProctorHttpClient proctorHttpClient,
                                final ProctorServerConfiguration localServerConfiguration) {

        this.applicationEventPublisher = applicationEventPublisher;
        this.proctorRouteHandlerManager = proctorRouteHandlerManager;
        this.proctorHttpClient = proctorHttpClient;
        this.localServerConfiguration = localServerConfiguration;

    }

    /**
     * Process incoming request for proxy call
     *
     * @param request to process
     * @return full http response
     */
    FullHttpResponse processRequest(final FullHttpRequest request)
            throws InterruptedException,
                   ExecutionException,
                   TimeoutException,
                   IOException,
                   InputNotMatchedException,
                   NoHandleForNameException {

        final FullHttpResponse response;

        final String clientRequestPath = request.getUri();

        final Optional<ProctorHandlerConfiguration> optionalProperties =
                proctorRouteHandlerManager.getMatchingPropertiesFor(clientRequestPath);

        final Optional<ProctorRouteHandler> optionalHandler =
                optionalProperties.isPresent() ?
                    proctorRouteHandlerManager.getHandler(optionalProperties.get()) :
                    Optional.empty();

        if (optionalHandler.isPresent()) {

            final ProctorRouteHandler matchingProctorRouteHandler = optionalHandler.get();

            applicationEventPublisher.publishEvent(new ProxyHandlerMatchedEvent(clientRequestPath));

            final URL proxyURL = matchingProctorRouteHandler.resolveURLFor(clientRequestPath,
                                                                           optionalProperties.get());

            //
            // Redirect request to handler generated URL
            //
            final String proxyURLExternalForm = proxyURL.toExternalForm();

            if (log.isTraceEnabled()) {

                log.trace("Proxying {} request for URI '{}' to '{}'.",
                          request.getMethod(),
                          clientRequestPath,
                          proxyURLExternalForm);

            }

            final QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());

            final HttpRequestData proxyRequest = new HttpRequestData(proxyURL.getProtocol(),
                                                                     InetAddress.getByName(proxyURL.getHost()),
                                                                     proxyURL.getPort(),
                                                                     request.getMethod().name(),
                                                                     extractHeaders(request.headers()),
                                                                     extractRequestContent(request.content()),
                                                                     queryStringDecoder.parameters(),
                                                                     queryStringDecoder.uri());

            applicationEventPublisher.publishEvent(new ProxyRequestReceivedEvent(proxyRequest));

            final HttpResponseData httpResponseData = proctorHttpClient.execute(proxyRequest);

            applicationEventPublisher.publishEvent(new ProxyReplySentEvent(httpResponseData));

            response = new DefaultFullHttpResponse(request.getProtocolVersion(),
                                                   HttpResponseStatus.valueOf(httpResponseData.getStatusCode()),
                                                   copiedBuffer(httpResponseData.getBody()));

            handleResponseHeaders(httpResponseData,
                                  response,
                                  proxyURL);

            if (log.isTraceEnabled()) {

                log.trace("Returning response for {} to URI '{}'.",
                          request.getMethod(),
                          clientRequestPath);

            }

        } else {


            if (log.isTraceEnabled()) {

                log.trace("No matching handler found for {} request to URI '{}'.",
                          request.getMethod(),
                          clientRequestPath);

            }


            applicationEventPublisher.publishEvent(new ProxyHandlerNotMatchedEvent(clientRequestPath));

            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                                                   HttpResponseStatus.NOT_FOUND);

        }

        return response;

    }

    /**
     * Extract bytes from buffer, careful not to use any apparent methods.
     *
     * @param byteBuf to extract
     * @return byte array of buffer
     */
    byte[] extractRequestContent(ByteBuf byteBuf) {

        //
        // Not all byte buffers have backing arrays, must read manually
        // (because implementing this inside the ByteBuf wouldn't be super secret and
        // its better to throw unsupported operation anyways...)
        //
        if(byteBuf.hasArray()) {

            return byteBuf.array();

        } else {

            final byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            return bytes;

        }

    }

    /**
     * Extract all headers to a map for proxy call
     *
     * @param headers to extract
     * @return map with header name as key and its values
     */
    Map<String, Collection<String>> extractHeaders(final HttpHeaders headers) {

        final Map<String, Collection<String>> headersMap = new HashMap<>();

        for (String name : headers.names()) {
            if(headersMap.containsKey(name)) {
                headersMap.get(name).addAll(headers.getAll(name));
            } else {
                headersMap.put(name, headers.getAll(name));
            }
        }

        return headersMap;

    }

    void handleResponseHeaders(final HttpResponseData responseData,
                               final HttpResponse response,
                               final URL proxyUrl)
            throws MalformedURLException {

        URL originURL = localServerConfiguration.getProxyOriginURL();

        response.setStatus(new HttpResponseStatus(responseData.getStatusCode(),
                                                  responseData.getStatusMessage()));

        response.headers()
                .add(HttpHeaders.Names.CONTENT_LENGTH,
                     responseData.getContentLength());

        if (StringUtils.isNotBlank(responseData.getContentType())) {

            response.headers()
                    .add(HttpHeaders.Names.CONTENT_TYPE,
                         responseData.getContentType());

        }

        final Map<String, Collection<String>> responseHeaders = responseData.getHeaders();

        for (final Map.Entry<String, Collection<String>> responseHeader : responseHeaders.entrySet()) {

            response.headers()
                    .set(responseHeader.getKey(),
                         responseHeader.getValue()
                                 .stream()
                                 .map(value -> value.replaceAll(proxyUrl.toString(),
                                                                originURL.toString()))
                                 .collect(Collectors.toList()));

        }

    }

    /**
     * Genereate a plain text full HTTP Response
     *
     * @param statusCode    of remote call
     * @param statusMessage of remote call
     * @return Plain text http response
     */
    HttpResponse createTextResponse(final HttpResponseStatus statusCode,
                                    final String statusMessage) {

        DefaultFullHttpResponse response = null;

        final String msg = Integer.toString(statusCode.code()) +
                           "\n\n" +
                           ((statusMessage == null) ?
                            "" :
                            statusMessage) +
                           "\n\n";

        try {

            final byte[] bytes = msg.getBytes("UTF-8");

            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                                                   statusCode,
                                                   Unpooled.wrappedBuffer(bytes));

            HttpHeaders.addHeader(response,
                                  HttpHeaders.Names.CONTENT_TYPE,
                                  "text/plain");

            HttpHeaders.addHeader(response,
                                  HttpHeaders.Names.CONTENT_LENGTH,
                                  bytes.length);

        } catch (UnsupportedEncodingException e) {

            //
            // JVM is required to support UTF-8
            //
            log.error("UTF-8 encoding not supported by JVM");

        }

        return response;

    }

}
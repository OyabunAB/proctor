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

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import se.oyabun.proctor.events.handler.ProxyHandlerMatchedEvent;
import se.oyabun.proctor.events.handler.ProxyHandlerNotMatchedEvent;
import se.oyabun.proctor.events.http.ProxyReplySentEvent;
import se.oyabun.proctor.events.http.ProxyRequestReceivedEvent;
import se.oyabun.proctor.exceptions.InputNotMatchedException;
import se.oyabun.proctor.exceptions.NoHandleForNameException;
import se.oyabun.proctor.handler.ProctorRouteHandler;
import se.oyabun.proctor.handler.manager.ProctorRouteHandlerManager;
import se.oyabun.proctor.handler.properties.ProctorHandlerProperties;
import se.oyabun.proctor.http.HttpRequestData;
import se.oyabun.proctor.http.HttpResponseData;
import se.oyabun.proctor.http.client.ProctorHttpClient;
import se.oyabun.proctor.util.lang.AsciiUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static io.netty.buffer.Unpooled.copiedBuffer;

/**
 * Proctor Netty Http Responder
 */
@Component
public class ProctorHttpResponder {

    private static final Logger logger = LoggerFactory.getLogger(ProctorHttpResponder.class);
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ProctorRouteHandlerManager proctorRouteHandlerManager;
    private final ProctorHttpClient proctorHttpClient;

    @Value("${se.oyabun.proctor.proxy.local.keystore.path:#{null}}")
    private String keystorePath;

    @Value("${se.oyabun.proctor.proxy.local.keystore.password:#{null}}")
    private String keyStorePassword;

    @Value("${se.oyabun.proctor.proxy.listen.port}")
    private int proxyListenPort;

    @Value("${se.oyabun.proctor.proxy.listen.address}")
    private String proxyListenAddress;

    @Autowired
    public ProctorHttpResponder(final ApplicationEventPublisher applicationEventPublisher,
                                final ProctorRouteHandlerManager proctorRouteHandlerManager,
                                final ProctorHttpClient proctorHttpClient) {

        this.applicationEventPublisher = applicationEventPublisher;
        this.proctorRouteHandlerManager = proctorRouteHandlerManager;
        this.proctorHttpClient = proctorHttpClient;

    }

    /**
     * Process incomming request for proxying
     *
     * @param request to process
     * @return full http response
     */
    FullHttpResponse processRequest(final FullHttpRequest request)
            throws
            InterruptedException,
            ExecutionException,
            TimeoutException,
            IOException,
            InputNotMatchedException,
            NoHandleForNameException {

        final FullHttpResponse response;

        final Map<String, List<String>> headers = new HashMap<>();
        extractHeaders(request,
                       headers);

        final String clientRequestPath = request.getUri();

        final Optional<ProctorHandlerProperties> optionalProperties =
                proctorRouteHandlerManager.getMatchingPropertiesFor(clientRequestPath)
                                          .findFirst();

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

            if (logger.isTraceEnabled()) {

                logger.trace("Proxying request for URI '{}' to '{}'.",
                             clientRequestPath,
                             proxyURLExternalForm);

            }

            final QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());

            final HttpRequestData proxyRequest = new HttpRequestData(proxyURL.getProtocol(),
                                                                     InetAddress.getByName(proxyURL.getHost()),
                                                                     proxyURL.getPort(),
                                                                     request.getMethod().name(),
                                                                     headers,
                                                                     request.content().array(),
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

            if (logger.isTraceEnabled()) {

                logger.trace("Returning response for URI '{}'.",
                             clientRequestPath);

            }

        } else {


            if (logger.isTraceEnabled()) {

                logger.trace("No matching handler found for request for URI '{}'.",
                             clientRequestPath);

            }


            applicationEventPublisher.publishEvent(new ProxyHandlerNotMatchedEvent(clientRequestPath));

            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                                                   HttpResponseStatus.NOT_FOUND);

        }

        return response;

    }

    void extractHeaders(final HttpRequest request,
                        final Map<String, List<String>> headers) {

        for (String headername : request.headers()
                                        .names()) {

            Iterable<String> headerValues = request.headers()
                                                   .getAll(headername);

            ArrayList<String> headerValuesCopy = new ArrayList<>();

            for (String headerValue : headerValues) {

                headerValuesCopy.add(headerValue);

            }

            headers.put(headername,
                        headerValuesCopy);

        }

    }

    void handleResponseHeaders(final HttpResponseData responseData,
                               final HttpResponse response,
                               final URL proxyUrl)
            throws
            MalformedURLException {

        URL originURL = new URL(((StringUtils.isNotBlank(keyStorePassword) && StringUtils.isNotBlank(keystorePath)) ?
                                 "https" :
                                 "http") + "://" + proxyListenAddress + ":" + proxyListenPort + "/");

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

        final Map<String, List<String>> responseHeaders = responseData.getHeaders();

        for (final Map.Entry<String, List<String>> responseHeader : responseHeaders.entrySet()) {

            final List<String> responseHeaderValues = responseHeader.getValue();
            final List<String> rewrittenHeaderValues = new ArrayList<>();

            for (String responseHeaderValue : responseHeaderValues) {

                rewrittenHeaderValues.add(responseHeaderValue.replaceAll(proxyUrl.toString(),
                                                                         originURL.toString()));

            }

            response.headers()
                    .set(responseHeader.getKey(),
                         responseHeaderValues);

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

        final String msg = AsciiUtil.generateAsciiArtText(Integer.toString(statusCode.code())) +
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
            logger.error("UTF-8 encoding not supported by JVM");

        }

        return response;

    }

}
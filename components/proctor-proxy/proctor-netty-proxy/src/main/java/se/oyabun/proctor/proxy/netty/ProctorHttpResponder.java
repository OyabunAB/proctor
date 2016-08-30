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
import se.oyabun.proctor.events.ProxyReplySentEvent;
import se.oyabun.proctor.events.ProxyRequestReceivedEvent;
import se.oyabun.proctor.exceptions.InputNotMatchedException;
import se.oyabun.proctor.exceptions.NoHandleForNameException;
import se.oyabun.proctor.handler.ProctorRouteHandler;
import se.oyabun.proctor.handler.manager.ProctorRouteHandlerManager;
import se.oyabun.proctor.http.HttpRequestData;
import se.oyabun.proctor.http.HttpResponseData;
import se.oyabun.proctor.http.client.ProctorHttpClient;
import se.oyabun.proctor.util.lang.AsciiUtil;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
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

    private ApplicationEventPublisher applicationEventPublisher;

    private ProctorRouteHandlerManager proctorRouteHandlerManager;

    private ProctorHttpClient proctorHttpClient;

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
     * @param request to process
     * @return full http response
     */
    FullHttpResponse processRequest(final FullHttpRequest request)
            throws InterruptedException, ExecutionException, TimeoutException, IOException,
                   InputNotMatchedException, NoHandleForNameException {

        final FullHttpResponse response;

        final Map<String, List<String>> headers = new HashMap<>();
        extractHeaders(request, headers);

        final String clientRequestPath = request.getUri();

        final Optional<ProctorRouteHandler> optionalHandler =
                proctorRouteHandlerManager.getRegisteredRouteHandlers()
                        .stream()
                        .filter(proctorProctorRouteHandler ->
                                proctorProctorRouteHandler.matches(clientRequestPath))
                        .findFirst();

        if (optionalHandler.isPresent()) {

            final ProctorRouteHandler matchingProctorRouteHandler = optionalHandler.get();

            final URL proxyURL =
                    matchingProctorRouteHandler.resolveURLFor(
                            matchingProctorRouteHandler.getHandleNameFor(clientRequestPath),
                            clientRequestPath);

            //
            // Redirect request to handler generated URL
            //
            final String proxyURLExternalForm = proxyURL.toExternalForm();

            if (logger.isTraceEnabled()) {

                logger.trace(
                        "Proxying request for URI '{}' to '{}'.",
                        clientRequestPath,
                        proxyURLExternalForm);

            }

            final QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());

            final HttpRequestData proxyRequest =
                    new HttpRequestData(
                            request.getProtocolVersion().protocolName(),
                            InetAddress.getByName(proxyURL.getHost()),
                            proxyURL.getPort(),
                            request.getMethod().name(),
                            headers,
                            request.content().array(),
                            queryStringDecoder.toString(),
                            queryStringDecoder.path());

            applicationEventPublisher.publishEvent(new ProxyRequestReceivedEvent(proxyRequest));

            final HttpResponseData httpResponseData = proctorHttpClient.execute(proxyRequest);

            applicationEventPublisher.publishEvent(new ProxyReplySentEvent(httpResponseData));

            response = new DefaultFullHttpResponse(
                    request.getProtocolVersion(),
                    HttpResponseStatus.valueOf(httpResponseData.getStatusCode()),
                    copiedBuffer(httpResponseData.getBody()));

            handleResponseHeaders(httpResponseData, response);

        } else {

            if(logger.isTraceEnabled()) {

                logger.trace("No matching handler found for request for URI '{}'.",
                        clientRequestPath);

            }

            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);

        }

        return response;

    }

    void handleResponseHeaders(final HttpResponseData responseData,
                               final HttpResponse response) {

        response.setStatus(new HttpResponseStatus(responseData.getStatusCode(), responseData.getStatusMessage()));

        response.headers().add(HttpHeaders.Names.CONTENT_LENGTH, responseData.getContentLength());

        if(StringUtils.isNotBlank(responseData.getContentType())) {

            response.headers().add(HttpHeaders.Names.CONTENT_TYPE, responseData.getContentType());

        }

        final Map<String, List<String>> responseHeaders = responseData.getHeaders();

        for (final Map.Entry<String, List<String>> responseHeader : responseHeaders.entrySet()) {

            final List<String> responseHeaderValues = responseHeader.getValue();

            response.headers().set(responseHeader.getKey(), responseHeaderValues);

        }

    }

    void extractHeaders(final HttpRequest request,
                        final Map<String, List<String>> headers) {

        for (String headername : request.headers().names()) {

            Iterable<String> headerValues = request.headers().getAll(headername);

            ArrayList<String> headerValuesCopy = new ArrayList<>();

            for (String headerValue : headerValues) {

                headerValuesCopy.add(headerValue);

            }

            headers.put(headername, headerValuesCopy);

        }

    }

    CharArrayWriter getRequestBody(final FullHttpRequest request)
            throws IOException {

        //
        // Use char array to read chunks instead
        //
        CharArrayWriter requestBuffer = new CharArrayWriter();
        final ByteBuf requestReader = request.content();

        for (int charRead = requestReader.readInt(); charRead >= 0; charRead = requestReader.readChar()) {

            requestBuffer.write(charRead);

        }

        return requestBuffer;

    }

    /**
     * Genereate a plain text full HTTP Response
     * @param statusCode of remote call
     * @param statusMessage of remote call
     * @return Plain text http response
     */
    HttpResponse createTextResponse(final HttpResponseStatus statusCode,
                                    final String statusMessage) {

        DefaultFullHttpResponse response = null;

        final String msg =
                AsciiUtil.generateAsciiArtText(Integer.toString(statusCode.code())) + "\n\n" +
                        ((statusMessage == null) ? "" : statusMessage) + "\n\n";

        try {

            final byte[] bytes = msg.getBytes("UTF-8");

            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, statusCode, Unpooled.wrappedBuffer(bytes));

            HttpHeaders.addHeader(response, HttpHeaders.Names.CONTENT_TYPE, "text/plain");

            HttpHeaders.addHeader(response, HttpHeaders.Names.CONTENT_LENGTH, bytes.length);

        } catch (UnsupportedEncodingException e) {

            //
            // JVM is required to support UTF-8
            //
            logger.error("UTF-8 encoding not supported by JVM");

        }

        return response;

    }

}
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
package se.oyabun.proctor.proxy.grizzly;

import org.glassfish.grizzly.http.io.NIOReader;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import se.oyabun.proctor.events.http.ProxyReplySentEvent;
import se.oyabun.proctor.events.http.ProxyRequestReceivedEvent;
import se.oyabun.proctor.handler.ProctorRouteHandler;
import se.oyabun.proctor.handler.manager.ProctorRouteHandlerManager;
import se.oyabun.proctor.handler.properties.ProctorHandlerProperties;
import se.oyabun.proctor.http.HttpRequestData;
import se.oyabun.proctor.http.HttpResponseData;
import se.oyabun.proctor.http.client.ProctorHttpClient;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.*;

/**
 * Proctor Grizzly HTTP Handler
 */
public class ProctorGrizzlyHttpHandler
        extends HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(ProctorGrizzlyHttpHandler.class);

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ProctorRouteHandlerManager proctorRouteHandlerManager;

    @Autowired
    private ProctorHttpClient httpClient;

    @Override
    public void service(final Request request,
                        final Response response)
            throws Exception {

        CharArrayWriter requestBuffer = getRequestBody(request);
        final String requestBodyAsString = requestBuffer.toString();

        final Map<String, List<String>> headers = new HashMap<>();
        extractHeaders(request, headers);

        final String clientRequestPath = request.getHttpHandlerPath();

        Optional<ProctorHandlerProperties> optionalProperties =
                proctorRouteHandlerManager.getMatchingPropertiesFor(clientRequestPath)
                .findFirst();

        Optional<ProctorRouteHandler> optionalHandler =
                optionalProperties.isPresent() ?
                    proctorRouteHandlerManager.getHandler(optionalProperties.get()) :
                    Optional.empty();

        if (optionalHandler.isPresent()) {


            final ProctorRouteHandler matchingProctorRouteHandler = optionalHandler.get();

            final URL proxyURL =
                    matchingProctorRouteHandler.resolveURLFor(
                            clientRequestPath,
                            optionalProperties.get());

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

            final String protocol = proxyURL.getProtocol();
            final String method = request.getMethod().getMethodString();
            final Map<String, List<String>> queryParameters = new HashMap<>();
            for (String parameterName : request.getParameterNames()) {
                queryParameters.put(parameterName, Arrays.asList(request.getParameterValues(parameterName)));
            }

            final HttpRequestData httpRequestData =
                    new HttpRequestData(
                            protocol,
                            InetAddress.getByName(proxyURL.getHost()),
                            proxyURL.getPort(),
                            method,
                            headers,
                            requestBodyAsString.getBytes(),
                            queryParameters,
                            clientRequestPath);

            applicationEventPublisher.publishEvent(new ProxyRequestReceivedEvent(httpRequestData));

            final HttpResponseData httpResponseData = httpClient.execute(httpRequestData);

            applicationEventPublisher.publishEvent(new ProxyReplySentEvent(httpResponseData));

            copyResponseHeaders(httpResponseData, response);

            writeResponseData(httpResponseData, response);

            if (logger.isTraceEnabled()) {

                logger.trace(
                        "Returning response for URI '{}'.",
                        clientRequestPath);

            }

        } else {

            if (logger.isTraceEnabled()) {

                logger.trace("No matching handler found for request for URI '{}'.",
                        clientRequestPath);

            }

        }

    }

    private void writeResponseData(final HttpResponseData responseData,
                                   final Response response) {

        response.setContentType(responseData.getContentType());

        response.setContentLengthLong(responseData.getContentLength());

        response.setCharacterEncoding("UTF-8");

        final byte[] responseDataBody = responseData.getBody();

        try {

            //
            // Write bytes to response
            //
            response.getNIOOutputStream().write(responseDataBody, 0, responseDataBody.length);

        } catch (IOException e) {

            logger.error("Failed to write response body.", e);

        }

    }

    private void copyResponseHeaders(final HttpResponseData responseData,
                                     final Response response) {

        response.setStatus(responseData.getStatusCode(), responseData.getStatusMessage());

        final Map<String, List<String>> responseHeaders = responseData.getHeaders();

        for (final Map.Entry<String, List<String>> responseHeader : responseHeaders.entrySet()) {

            final List<String> responseHeaderValues = responseHeader.getValue();

            for (final String responseHeaderValue : responseHeaderValues) {

                response.addHeader(responseHeader.getKey(), responseHeaderValue);

            }

        }

    }

    private void extractHeaders(final Request request,
                                final Map<String, List<String>> headers) {

        for (String headername : request.getHeaderNames()) {

            Iterable<String> headerValues = request.getHeaders(headername);
            ArrayList<String> headerValuesCopy = new ArrayList<>();

            for (String headerValue : headerValues) {

                headerValuesCopy.add(headerValue);

            }

            headers.put(headername, headerValuesCopy);

        }

    }

    private CharArrayWriter getRequestBody(final Request request)
            throws IOException {

        //
        // Use char array to read chunks instead
        //
        CharArrayWriter requestBuffer = new CharArrayWriter();
        final NIOReader requestReader = request.getNIOReader();

        for (int charRead = requestReader.read(); charRead >= 0; charRead = requestReader.read()) {

            requestBuffer.write(charRead);

        }

        return requestBuffer;

    }

}

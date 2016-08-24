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
package se.oyabun.proctor.proxy.litteproxy.filters;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.apache.commons.lang3.StringUtils;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.oyabun.proctor.handler.ProctorRouteHandler;
import se.oyabun.proctor.util.lang.AsciiUtil;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Proctor handler enabled HTTP Filter for Litte Proxy
 * @author Daniel Sundberg
 * @author Johan Maasing
 */
@Component
public class ProctorHandlersHTTPFilter
        implements HttpFilters {

    /**
     * Regex for grouping parts of a URL
     */
    private static final Pattern URL_PATTERN = Pattern.compile("(^[^#]*?://)[^/]*(/.*)");

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private List<ProctorRouteHandler> registeredProctorProctorRouteHandlers;

    @Value("${se.oyabun.proctor.proxy.source.address}")
    private String sourceHostname;

    @Value("${se.oyabun.proctor.proxy.source.port}")
    private int sourcePort;


    /**
     * Filters requests on their way from the client to the proxy. To interrupt processing of this request and return a
     * response to the client immediately, return an HttpResponse here. Otherwise, return null to continue processing as
     * usual.
     *
     *
     * <b>Important:</b> When returning a response, you must include a mechanism to allow the client to determine the length
     * of the message (see RFC 7230, section 3.3.3: https://tools.ietf.org/html/rfc7230#section-3.3.3 ). For messages that
     * may contain a body, you may do this by setting the Transfer-Encoding to chunked, setting an appropriate
     * Content-Length, or by adding a "Connection: close" header to the response (which will instruct LittleProxy to close
     * the connection). If the short-circuit response contains body content, it is recommended that you return a
     * FullHttpResponse.
     *
     * @param httpObject Client to Proxy HttpRequest (and HttpContent, if chunked)
     * @return a short-circuit response, or null to continue processing as usual
     */
    public HttpResponse clientToProxyRequest(final HttpObject httpObject) {

        if (httpObject instanceof HttpRequest) {

            final HttpRequest httpRequest = (HttpRequest) httpObject;

            if (log.isTraceEnabled()) {

                log.trace(
                        "Client to proxy {} request received for '{} {}'.",
                        httpRequest.getProtocolVersion(),
                        httpRequest.getMethod(),
                        httpRequest.getUri());

            }

            final String clientRequestURI = httpRequest.getUri();

            try {

                Optional<ProctorRouteHandler> optionalHandler =
                        registeredProctorProctorRouteHandlers
                                .stream()
                                .filter(proctorProctorRouteHandler ->
                                        proctorProctorRouteHandler.matches(clientRequestURI))
                                .findFirst();

                if (optionalHandler.isPresent()) {

                    final ProctorRouteHandler matchingProctorRouteHandler = optionalHandler.get();

                    final URL proxyURL =
                            matchingProctorRouteHandler.resolveURLFor(
                                    matchingProctorRouteHandler.getHandleNameFor(clientRequestURI),
                                    clientRequestURI);

                    //
                    // Redirect request to handler generated URL
                    //
                    final String proxyURLExternalForm = proxyURL.toExternalForm();

                    if (log.isDebugEnabled()) {

                        log.debug(
                                "Proxying request for URI '{}' to '{}' with {}.",
                                clientRequestURI,
                                proxyURLExternalForm,
                                matchingProctorRouteHandler.getHandleNameFor(clientRequestURI));

                    }

                    httpRequest.headers().set(HttpHeaders.Names.HOST, proxyURL.getHost());

                    httpRequest.setUri(proxyURLExternalForm);

                } else {

                    return createTextResponse(
                            HttpResponseStatus.NOT_FOUND,
                            "The proxy could not find any registered handler for: " + clientRequestURI);

                }

            } catch (MalformedURLException e) {

                return createTextResponse(
                        HttpResponseStatus.SERVICE_UNAVAILABLE,
                        "Malformed URL: " + clientRequestURI);

            } catch (Exception e) {

                log.error("Problems during handler processing.", e);

                return createTextResponse(
                        HttpResponseStatus.SERVICE_UNAVAILABLE,
                        "ProctorRouteHandler is not responding properly: " + clientRequestURI);

            }

        } else {

            if(log.isTraceEnabled()) {

                log.trace("Client to proxy object ignored for {}.",
                        httpObject.getClass().getSimpleName());

            }

        }

        //
        // If something returns, proxy chain will return to client
        //
        return null;

    }

    /**
     * Genereate a plain text full HTTP Response
     * @param statusCode of remote call
     * @param statusMessage of remote call
     * @return Plain text http response
     */
    private HttpResponse createTextResponse(final HttpResponseStatus statusCode,
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
            log.error("UTF-8 encoding not supported by JVM");

        }

        return response;

    }

    /**
     * Filters requests on their way from the proxy to the server. To interrupt processing of this request and return a
     * response to the client immediately, return an HttpResponse here. Otherwise, return null to continue processing as
     * usual.
     *
     * <b>Important:</b> When returning a response, you must include a mechanism to allow the client to determine the length
     * of the message (see RFC 7230, section 3.3.3: https://tools.ietf.org/html/rfc7230#section-3.3.3 ). For messages that
     * may contain a body, you may do this by setting the Transfer-Encoding to chunked, setting an appropriate
     * Content-Length, or by adding a "Connection: close" header to the response. (which will instruct LittleProxy to close
     * the connection). If the short-circuit response contains body content, it is recommended that you return a
     * FullHttpResponse.
     *
     * @param httpObject Proxy to Server HttpRequest (and HttpContent, if chunked)
     * @return a short-circuit response, or null to continue processing as usual
     */
    public HttpResponse proxyToServerRequest(final HttpObject httpObject) {

        if(log.isTraceEnabled()) {

            log.trace("Proxy to server request prepared.");

        }

        return null;

    }

    /**
     * Informs filter that proxy to server request is being sent.
     */
    @Override
    public void proxyToServerRequestSending() {

        if(log.isTraceEnabled()) {

            log.trace("Proxy to server request is being sent.");

        }

    }

    /**
     * Informs filter that the HTTP request, including any content, has been sent.
     */
    @Override
    public void proxyToServerRequestSent() {

    }

    /**
     * Filters responses on their way from the server to the proxy.
     *
     * @param httpObject Server to Proxy HttpResponse (and HttpContent, if chunked)
     * @return the modified (or unmodified) HttpObject. Returning null will
     * force a disconnect.
     */
    public HttpObject serverToProxyResponse(final HttpObject httpObject) {

        if (httpObject instanceof HttpResponse) {

            HttpResponse httpResponse = (HttpResponse) httpObject;

            if(log.isDebugEnabled()) {

                log.debug("Original " + parseHeaders(httpResponse));

            }

            filterHeaders(httpResponse);

            if(log.isDebugEnabled()) {

                log.debug("Filtered " + parseHeaders(httpResponse));

            }

        }

        return httpObject;

    }

    /**
     * Informs filter that a timeout occurred before the server response was received by the client. The timeout may have
     * occurred while the client was sending the request, waiting for a response, or after the client started receiving
     * a response (i.e. if the response from the server "stalls").
     *
     * See {@link HttpProxyServerBootstrap#withIdleConnectionTimeout(int)} for information on setting the timeout.
     */
    @Override
    public void serverToProxyResponseTimedOut() {

        if(log.isTraceEnabled()) {

            log.trace("Server to proxy response timed out.");

        }

    }

    /**
     * Informs filter that server to proxy response is being received.
     */
    @Override
    public void serverToProxyResponseReceiving() {

        if(log.isTraceEnabled()) {

            log.trace("Server to proxy response is being received.");

        }

    }

    /**
     * Informs filter that server to proxy response has been received.
     */
    @Override
    public void serverToProxyResponseReceived() {

        if(log.isTraceEnabled()) {

            log.trace("Server to proxy response has been received.");

        }

    }

    /**
     * Filters responses on their way from the proxy to the client.
     *
     * @param httpObject Proxy to Client HttpResponse (and HttpContent, if chunked)
     * @return the modified (or unmodified) HttpObject. Returning null will
     * force a disconnect.
     */
    @Override
    public HttpObject proxyToClientResponse(final HttpObject httpObject) {

        if(log.isTraceEnabled()) {

            log.trace("Proxy to client response is being sent.");

        }

        return httpObject;

    }

    /**
     * Informs filter that proxy to server connection is in queue.
     */
    @Override
    public void proxyToServerConnectionQueued() {

        if(log.isTraceEnabled()) {

            log.trace("Proxy to server connection is queued.");

        }

    }

    /**
     * Filter DNS resolution from proxy to server.
     *
     * @param resolvingServerHostAndPort Server "HOST:PORT"
     * @return alternative address resolution. Returning null will let normal
     * DNS resolution continue.
     */
    @Override
    public InetSocketAddress proxyToServerResolutionStarted(final String resolvingServerHostAndPort) {

        if(log.isTraceEnabled()) {

            log.trace("Proxy to server resolution for '{}' has started.", resolvingServerHostAndPort);

        }

        return null;

    }

    /**
     * Informs filter that proxy to server DNS resolution failed for the specified host and port.
     *
     * @param hostAndPort hostname and port the proxy failed to resolve
     */
    @Override
    public void proxyToServerResolutionFailed(final String hostAndPort) {

        if(log.isTraceEnabled()) {

            log.trace("Proxy to server resolution for '{}' has failed.", hostAndPort);

        }

    }

    /**
     * Informs filter that proxy to server DNS resolution has happened.
     *
     * @param serverHostAndPort     Server "HOST:PORT"
     * @param resolvedRemoteAddress Address it was proxyToServerResolutionSucceeded to
     */
    @Override
    public void proxyToServerResolutionSucceeded(final String serverHostAndPort,
                                                 final InetSocketAddress resolvedRemoteAddress) {

        if(log.isTraceEnabled()) {

            log.trace(
                    "Proxy to server resolution of '{}' has succeded, resolved '{}:{}'.",
                    serverHostAndPort,
                    resolvedRemoteAddress.getAddress(),
                    resolvedRemoteAddress.getPort());

        }

    }

    /**
     * Informs filter that proxy to server connection is initiating.
     */
    @Override
    public void proxyToServerConnectionStarted() {

        if(log.isTraceEnabled()) {

            log.trace("Proxy to server connection started.");

        }

    }

    /**
     * Informs filter that proxy to server ssl handshake is initiating.
     */
    @Override
    public void proxyToServerConnectionSSLHandshakeStarted() {

        if(log.isTraceEnabled()) {

            log.trace("Proxy to server SSL handshake started.");

        }

    }

    /**
     * Informs filter that proxy to server connection has failed.
     */
    @Override
    public void proxyToServerConnectionFailed() {

        if(log.isTraceEnabled()) {

            log.trace("Proxy to server connection failed.");

        }

    }

    /**
     * Informs filter that proxy to server connection has succeeded.
     *
     * @param serverCtx the {@link io.netty.channel.ChannelHandlerContext} used to connect to the server
     */
    @Override
    public void proxyToServerConnectionSucceeded(final ChannelHandlerContext serverCtx) {

        if(log.isTraceEnabled()) {

            log.trace("Proxy to server connection succeded, context '{}' created.", serverCtx.name());

        }

    }

    /**
     * Create a logstring from given http response headers.
     *
     * @param httpResponse to parse
     * @return Formatted log string containing header information
     */
    public String parseHeaders(final HttpResponse httpResponse) {

        final StringBuilder responseHeaders = new StringBuilder("HttpResponse[headers:");

        httpResponse.headers().names()
                .forEach(name ->
                        responseHeaders.append(
                                "{'" + name + "':'" + (httpResponse.headers().get(name) + "'}")));

        responseHeaders.append("]");

        return responseHeaders.toString();

    }

    /**
     * Rewrite headers for source values
     *
     * @param httpResponse  to rewrite values for
     */
    public void filterHeaders(final HttpResponse httpResponse) {


        //
        // Restore location header to configured value
        //
        String locationValue = httpResponse.headers().get(HttpHeaders.Names.LOCATION);

        if (StringUtils.isNotEmpty(locationValue)) {

            Matcher urlMatcher = URL_PATTERN.matcher(locationValue);

            if (urlMatcher.matches()) {

                MatchResult matchResult = urlMatcher.toMatchResult();

                httpResponse.headers().set(
                        HttpHeaders.Names.LOCATION,
                        matchResult.group(1) + sourceHostname + ":" + sourcePort + matchResult.group(2));

            }

        }

    }

}

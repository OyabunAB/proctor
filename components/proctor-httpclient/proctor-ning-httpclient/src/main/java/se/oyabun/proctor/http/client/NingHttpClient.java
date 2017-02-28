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
package se.oyabun.proctor.http.client;

import com.ning.http.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.oyabun.proctor.http.HttpRequestData;
import se.oyabun.proctor.http.HttpResponseData;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Ning HTTP Client Proctor implementation
 */
@Component
public class NingHttpClient
        extends AbstractProctorHttpClient
        implements ProctorHttpClient {

    private static final String HTTPS = "https";
    private static final String HTTP = "http";
    private static final int MIN_PORT = 1;
    private static final int MAX_PORT = 65535;
    private static final int DEFAULT_CONNECT_TIMEOUT = 10000;
    private static final int DEFAULT_READ_TIMEOUT = 10000;
    private static final int DEFAULT_REQUEST_TIMEOUT = 60000;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private AsyncHttpClient asyncHttpClient;

    public HttpResponseData execute(final HttpRequestData request)
            throws IOException,
                   CancellationException,
                   InterruptedException,
                   ExecutionException,
                   TimeoutException {

        final String requestUrl = request.getProtocol() +
                                  "://" +
                                  request.getHost()
                                         .getHostName() +
                                  (request.getPort() != null ?
                                   ":" + request.getPort() :
                                   "") +
                                  request.getPath();

        final Request httpRequest = new RequestBuilder().setUrl(requestUrl)
                                                        .setHeaders(request.getHeaders())
                                                        .setMethod(request.getMethod())
                                                        .setBody(request.getBody())
                                                        .build();

        final ListenableFuture<Response> asyncResponse = asyncHttpClient.executeRequest(httpRequest);

        final Response response = asyncResponse.get(DEFAULT_REQUEST_TIMEOUT + 100,
                                                    TimeUnit.MILLISECONDS);



        final HttpResponseData responseData = new HttpResponseData(response.getStatusCode(),
                                                                   response.getStatusText(),
                                                                   extractHeaders(response.getHeaders()),
                                                                   response.getContentType(),
                                                                   response.getResponseBodyAsBytes().length,
                                                                   response.getResponseBodyAsBytes());

        return responseData;

    }

    private Map<String, Collection<String>> extractHeaders(Map<String, List<String>> originalHeaders) {

        Map<String, Collection<String>> headers = new HashMap<>();
        originalHeaders.forEach((s, strings) -> headers.put(s, strings));
        return headers;

    }

    /**
     * Init callback implementation.
     *
     * @throws Exception when something goes horribly woring with initing the client
     */
    public void initHttpClient()
            throws
            Exception {

        if (log.isDebugEnabled()) {

            log.debug("Initializing NING HTTP Client.");

        }

        AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder().setAcceptAnyCertificate(true)
                                                                          .setAllowPoolingConnections(true)
                                                                          .setUserAgent("Proctor Open Proxy Framework")
                                                                          .setConnectTimeout(DEFAULT_CONNECT_TIMEOUT)
                                                                          .setReadTimeout(DEFAULT_READ_TIMEOUT)
                                                                          .setRequestTimeout(DEFAULT_REQUEST_TIMEOUT)
                                                                          .build();

        this.asyncHttpClient = new AsyncHttpClient(config);

    }

    /**
     * Shutdown callback implementation.
     *
     * @throws Exception when something goes horribly wrong when shutting down the client
     */
    public void shutDownHttpClient()
            throws
            Exception {

        if (log.isDebugEnabled()) {

            log.debug("Stopping NING HTTP Client.");

        }

        this.asyncHttpClient.close();

    }

}

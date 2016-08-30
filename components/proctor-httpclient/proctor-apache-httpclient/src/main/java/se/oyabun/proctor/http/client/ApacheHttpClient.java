package se.oyabun.proctor.http.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.oyabun.proctor.http.HttpRequestData;
import se.oyabun.proctor.http.HttpResponseData;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Proctor Apache HTTP Client implementation
 */
@Component
public class ApacheHttpClient
        extends AbstractProctorHttpClient
        implements ProctorHttpClient {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final int DEFAULT_CONNECT_TIMEOUT = 10000;
    private static final int DEFAULT_READ_TIMEOUT = 10000;
    private static final int DEFAULT_REQUEST_TIMEOUT = 60000;

    private CloseableHttpAsyncClient httpclient;

    @Override
    public HttpResponseData execute(final HttpRequestData request)
            throws IOException, CancellationException, InterruptedException, ExecutionException, TimeoutException {


        final String requestUrl =
                request.getProtocol() + "://" + request.getHost().getHostName() +
                        (request.getPort() != null ? ":" + request.getPort() : "") +
                        request.getPath();

        RequestBuilder requestBuilder =
                RequestBuilder
                        .create(request.getMethod())
                        .setEntity(new ByteArrayEntity(request.getBody()))
                        .setUri(requestUrl);

        final HttpUriRequest httpRequest = requestBuilder.build();

        final Future<HttpResponse> asyncResponse = httpclient.execute(httpRequest, null);

        final HttpResponse response = asyncResponse.get(DEFAULT_REQUEST_TIMEOUT + 100, TimeUnit.MILLISECONDS);

        final Map<String, List<String>> responseHeaders = new HashMap<>();
        Arrays.stream(response.getAllHeaders())
                .map(header ->
                        responseHeaders.put(header.getName(), Arrays.asList(header.getValue())));

        return new HttpResponseData(
                response.getStatusLine().getStatusCode(),
                response.getStatusLine().getReasonPhrase(),
                responseHeaders,
                response.getEntity().getContentType().getValue(),
                response.getEntity().getContentLength(),
                EntityUtils.toByteArray(response.getEntity()));

    }

    @Override
    public void initHttpClient() throws Exception {

        log.debug("Initializing Apache HTTP Client.");

        httpclient = HttpAsyncClients.custom()
                .setSSLContext(
                        new SSLContextBuilder()
                                .loadTrustMaterial(null, (TrustStrategy) (chain, authType) -> true)
                                .setSecureRandom(new SecureRandom())
                                .build())
                .setSSLHostnameVerifier((variable, sslSession) -> true)
                .setDefaultRequestConfig(
                        RequestConfig
                                .custom()
                                .setConnectTimeout(DEFAULT_CONNECT_TIMEOUT)
                                .setConnectionRequestTimeout(DEFAULT_REQUEST_TIMEOUT)
                                .setSocketTimeout(DEFAULT_READ_TIMEOUT)
                                .build())
                .build();

        httpclient.start();

    }

    @Override
    public void shutDownHttpClient() throws Exception {

        log.debug("Stopping Apache HTTP Client.");

        httpclient.close();

    }

}

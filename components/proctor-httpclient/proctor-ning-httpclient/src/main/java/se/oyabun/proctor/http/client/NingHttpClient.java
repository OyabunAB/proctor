package se.oyabun.proctor.http.client;

import com.ning.http.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.oyabun.proctor.http.HttpRequestData;
import se.oyabun.proctor.http.HttpResponseData;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private static final Logger log = LoggerFactory.getLogger(NingHttpClient.class);

    private static final String HTTPS = "https";
    private static final String HTTP = "http";
    private static final int MIN_PORT = 1;
    private static final int MAX_PORT = 65535;

    private AsyncHttpClient asyncHttpClient;

    private static final int DEFAULT_CONNECT_TIMEOUT = 10000;
    private static final int DEFAULT_READ_TIMEOUT = 10000;
    private static final int DEFAULT_REQUEST_TIMEOUT = 60000;

    public HttpResponseData execute(final HttpRequestData request)
            throws IOException, CancellationException, InterruptedException, ExecutionException, TimeoutException {

        final String requestUrl =
                request.getProtocol() + "://" + request.getHost().getHostName() +
                        (request.getPort() != null ? ":" + request.getPort() : "") +
                        request.getPath();

        final Request httpRequest =
                new RequestBuilder()
                        .setUrl(requestUrl)
                        .setMethod(request.getMethod())
                        .setBody(request.getBody())
                        .build();

        final ListenableFuture<Response> asyncResponse = asyncHttpClient.executeRequest(httpRequest);

        Response response = asyncResponse.get(DEFAULT_REQUEST_TIMEOUT + 100, TimeUnit.MILLISECONDS);

        Map<String, List<String>> responseHeaders = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : response.getHeaders().entrySet()) {

            final String headerName = entry.getKey();
            final ArrayList<String> headerValues = new ArrayList<>(entry.getValue());
            responseHeaders.put(headerName, headerValues);

        }

        final HttpResponseData responseData =
                new HttpResponseData(
                        response.getStatusCode(),
                        response.getStatusText(),
                        responseHeaders,
                        response.getResponseBody());

        return responseData;

    }

    /**
     * Init callback implementation.
     * @throws Exception
     */
    public void initHttpClient() throws Exception {

        log.debug("Initializing NING HTTP Client.");

        AsyncHttpClientConfig.Builder config = new AsyncHttpClientConfig.Builder();

        config.setAcceptAnyCertificate(true);

        config.setAllowPoolingConnections(true);

        config.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);

        config.setReadTimeout(DEFAULT_READ_TIMEOUT);

        config.setRequestTimeout(DEFAULT_REQUEST_TIMEOUT);

        this.asyncHttpClient = new AsyncHttpClient(config.build());

    }

    /**
     * Shutdown callback implementation.
     * @throws Exception
     */
    public void shutDownHttpClient() throws Exception {

        log.debug("Stopping NING HTTP Client.");

        this.asyncHttpClient.close();

    }

}

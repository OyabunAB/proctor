package se.oyabun.proctor.http.client;

import se.oyabun.proctor.http.HttpRequestData;
import se.oyabun.proctor.http.HttpResponseData;

import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Proctor HTTP Client interface
 */
public interface ProctorHttpClient {

    /**
     * Execute given HTTP Request.
     * @param request to execute
     * @return wrapped HTTP Response
     * @throws IOException
     * @throws CancellationException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    HttpResponseData execute(final HttpRequestData request)
            throws IOException, CancellationException, InterruptedException, ExecutionException, TimeoutException;


    /**
     * Callback for init.
     * @throws Exception
     */
    void initHttpClient() throws Exception;

    /**
     * Callback for shutdown.
     * @throws Exception
     */
    void shutDownHttpClient() throws Exception;

}

package se.oyabun.proctor.http.client;

import org.springframework.stereotype.Component;
import se.oyabun.proctor.http.HttpRequestData;
import se.oyabun.proctor.http.HttpResponseData;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Abstract HTTP Client component for reducing boilerplate
 */
@Component
public abstract class AbstractProctorHttpClient
        implements ProctorHttpClient {

    public abstract HttpResponseData execute(HttpRequestData request)
            throws IOException, CancellationException, InterruptedException, ExecutionException, TimeoutException;

    @PostConstruct
    public abstract void initHttpClient() throws Exception;

    @PreDestroy
    public abstract void shutDownHttpClient() throws Exception;

}

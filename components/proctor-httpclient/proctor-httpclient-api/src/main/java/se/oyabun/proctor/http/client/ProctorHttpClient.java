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

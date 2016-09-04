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
package se.oyabun.proctor.http;

import java.util.List;
import java.util.Map;

/**
 * Specific Response part of HTTP wrapper.
 */
public class HttpResponseData
        extends HttpData {

    private final int statusCode;
    private final String statusMessage;
    private final String contentType;
    private final long contentLength;



    public HttpResponseData(final int statusCode,
                            final String statusMessage,
                            final Map<String, List<String>> headers,
                            final String contentType,
                            final long contentLength,
                            final byte[] body) {

        super(headers, body);
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.contentType = contentType;
        this.contentLength = contentLength;

    }

    public int getStatusCode() {

        return statusCode;

    }

    public String getStatusMessage() {

        return statusMessage;

    }

    public String getContentType() {

        return contentType;

    }

    public long getContentLength() {

        return contentLength;

    }

    @Override
    public String toString() {

        return "HttpResponseData{" +
                "statusCode=" + statusCode + ", " +
                "statusMessage=" + statusMessage + "} + " +
                super.toString();

    }

}

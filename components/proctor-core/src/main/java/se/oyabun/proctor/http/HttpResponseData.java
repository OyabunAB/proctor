package se.oyabun.proctor.http;

import java.util.List;
import java.util.Map;

/**
 * HTTP Response Data representation
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

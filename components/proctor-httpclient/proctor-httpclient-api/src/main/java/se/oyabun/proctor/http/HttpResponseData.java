package se.oyabun.proctor.http;

import java.util.List;
import java.util.Map;

/**
 *
 */
public class HttpResponseData
        extends HttpData {

    private final int statusCode;
    private final String statusMessage;

    public HttpResponseData(final int statusCode,
                            final String statusMessage,
                            final Map<String, List<String>> headers,
                            final String body) {

        super(headers, body);
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;

    }

    public int getStatusCode() {

        return statusCode;

    }

    public String getStatusMessage() {

        return statusMessage;

    }

    @Override
    public String toString() {

        return "HttpResponseData{" +
                "statusCode=" + statusCode + ", " +
                "statusMessage=" + statusMessage + "} + " +
                super.toString();

    }

}

package se.oyabun.proctor.http;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * HTTP Data wrapper
 */
public class HttpData {

    private final Map<String, List<String>> headers;
    private final byte[] body;

    public HttpData(final Map<String, List<String>> headers,
                    final byte[] body) {
        this.headers = headers;
        this.body = body;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.headers);
        hash = 37 * hash + Objects.hashCode(this.body);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) {

            return false;

        }

        if (getClass() != obj.getClass()) {

            return false;

        }

        final HttpData other = (HttpData) obj;

        if (!Objects.equals(this.getHeaders(), other.getHeaders())) {

            return false;
        }

        if (!Objects.equals(this.getBody(), other.getBody())) {

            return false;

        }

        return true;

    }

    @Override
    public String toString() {

        return "HttpData{" +
                "headers=" + headers + ", " +
                "body=" + body + '}';

    }

}
package se.oyabun.proctor.http;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * HTTP Data wrapper
 */
public class HttpData {

    private final Map<String, List<String>> headers;
    private final String body;

    public HttpData(Map<String, List<String>> headers, String body) {
        this.headers = headers;
        this.body = body;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public String getBody() {
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
        if (!Objects.equals(this.headers, other.headers)) {
            return false;
        }
        if (!Objects.equals(this.body, other.body)) {
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
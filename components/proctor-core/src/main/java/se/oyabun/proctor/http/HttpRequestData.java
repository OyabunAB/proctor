package se.oyabun.proctor.http;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 */
public class HttpRequestData
        extends HttpData {

    private final String protocol;
    private final String method;
    private final Map<String, List<String>> queryParameters;
    private final String path;
    private final Integer port;
    private final InetAddress host;

    public HttpRequestData(final String protocol,
                           final InetAddress host,
                           final Integer port,
                           final String method,
                           final Map<String, List<String>> headers,
                           final byte[] body,
                           final Map<String, List<String>> queryParameters,
                           final String path) {

        super(headers, body);
        this.port = port != -1 ? port : null;
        this.host = host;
        this.protocol = protocol;
        this.method = method;
        this.queryParameters = queryParameters;
        this.path = path;

    }

    public String getProtocol() {
        return protocol;
    }

    public String getMethod() {

        return method;

    }

    public Map<String, List<String>> getQueryParameters() {

        return queryParameters;

    }

    public String getPath() {

        return path;

    }

    public Integer getPort() {
        return port;
    }

    public InetAddress getHost() {
        return host;
    }

    @Override
    public int hashCode() {

        int hash = 7 * super.hashCode();
        hash = 67 * hash + Objects.hashCode(this.method);
        hash = 67 * hash + Objects.hashCode(this.queryParameters);
        hash = 67 * hash + Objects.hashCode(this.path);
        return hash;

    }

    @Override
    public boolean equals(Object obj) {

        if (!super.equals(obj)) {

            return false;

        }

        if (obj == null) {

            return false;

        }

        if (getClass() != obj.getClass()) {

            return false;

        }

        final HttpRequestData other = (HttpRequestData) obj;

        if (!Objects.equals(this.method, other.method)) {

            return false;

        }

        if (!Objects.equals(this.queryParameters, other.queryParameters)) {

            return false;

        }

        if (!Objects.equals(this.path, other.path)) {

            return false;

        }

        return true;

    }

    @Override
    public String toString() {

        return "HttpRequestData{" +
                "method=" + method + ", " +
                "path=" + path + ", " +
                "query=" + queryParameters + "} + " +
                super.toString();

    }

}

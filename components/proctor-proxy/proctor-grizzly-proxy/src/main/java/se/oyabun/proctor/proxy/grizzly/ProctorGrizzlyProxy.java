package se.oyabun.proctor.proxy.grizzly;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.grizzly.http.io.NIOReader;
import org.glassfish.grizzly.http.server.*;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.oyabun.proctor.handlers.ProctorRouteHandler;
import se.oyabun.proctor.http.HttpRequestData;
import se.oyabun.proctor.http.HttpResponseData;
import se.oyabun.proctor.http.client.ProctorHttpClient;
import se.oyabun.proctor.proxy.AbstractProctorProxy;
import se.oyabun.proctor.proxy.ProctorProxy;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.*;

/**
 *
 */
@Component
public class ProctorGrizzlyProxy
        extends AbstractProctorProxy
        implements ProctorProxy {

    private static final Logger log = LoggerFactory.getLogger(ProctorGrizzlyProxy.class);

    @Value("${se.oyabun.proctor.proxy.listen.port}")
    private int proxyListenPort;

    @Value("${se.oyabun.proctor.proxy.listen.address}")
    private String proxyListenAddress;

    @Value("${se.oyabun.proctor.proxy.local.keystore.path:#{null}}")
    private String keystorePath;

    @Value("${se.oyabun.proctor.proxy.local.keystore.password:#{null}}")
    private String keyStorePassword;

    @Autowired
    private List<ProctorRouteHandler> registeredProctorProctorRouteHandlers;

    @Autowired
    private ProctorHttpClient httpClient;

    private HttpServer httpServer;

    public ProctorGrizzlyProxy() {}

    public void startProxy() throws IOException {

        this.httpServer = HttpServer.createSimpleServer(null, proxyListenPort);

        if (StringUtils.isNotBlank(keystorePath) && StringUtils.isNotBlank(keyStorePassword)) {

            final NetworkListener listener = this.httpServer.getListener("grizzly");
            listener.setSecure(true);
            listener.setSSLEngineConfig(createSslConfiguration());
            log.debug("Listener: " + listener.getName());

        }

        this.httpServer.getServerConfiguration().addHttpHandler(new HttpHandler() {

            @Override
            public void service(final Request request,
                                final Response response)
                    throws Exception {

                CharArrayWriter requestBuffer = getRequestBody(request);
                final String requestBodyAsString = requestBuffer.toString();

                final Map<String, List<String>> headers = new HashMap<>();
                extractHeaders(request, headers);

                final String clientRequestURI = request.getHttpHandlerPath();

                Optional<ProctorRouteHandler> optionalHandler =
                        registeredProctorProctorRouteHandlers
                                .stream()
                                .filter(proctorProctorRouteHandler ->
                                        proctorProctorRouteHandler.matches(clientRequestURI))
                                .findFirst();

                if (optionalHandler.isPresent()) {


                    final ProctorRouteHandler matchingProctorRouteHandler = optionalHandler.get();

                    final URL proxyURL =
                            matchingProctorRouteHandler.resolveURLFor(
                                    matchingProctorRouteHandler.getHandleNameFor(clientRequestURI),
                                    clientRequestURI);

                    //
                    // Redirect request to handler generated URL
                    //
                    final String proxyURLExternalForm = proxyURL.toExternalForm();

                    if (log.isDebugEnabled()) {

                        log.debug(
                                "Proxying request for URI '{}' to '{}'.",
                                clientRequestURI,
                                proxyURLExternalForm);

                    }

                    final String protocol = proxyURL.getProtocol();
                    final String method = request.getMethod().getMethodString();
                    final String queryString = request.getQueryString();

                    final HttpRequestData requestData =
                            new HttpRequestData(
                                    protocol,
                                    InetAddress.getByName(proxyURL.getHost()),
                                    proxyURL.getPort(),
                                    method,
                                    headers,
                                    requestBodyAsString,
                                    queryString,
                                    clientRequestURI);

                    final HttpResponseData responseData = httpClient.execute(requestData);

                    copyResponseHeaders(responseData, response);
                    response.setStatus(responseData.getStatusCode(), responseData.getStatusMessage());
                    response.getWriter().write(responseData.getBody());

                }

            }

            private void copyResponseHeaders(final HttpResponseData responseData,
                                             final Response response) {

                final Map<String, List<String>> responseHeaders = responseData.getHeaders();

                for (final Map.Entry<String, List<String>> responseHeader : responseHeaders.entrySet()) {

                    final List<String> responseHeaderValues = responseHeader.getValue();

                    for (final String responseHeaderValue : responseHeaderValues) {

                        response.addHeader(responseHeader.getKey(), responseHeaderValue);

                    }

                }

            }

            private void extractHeaders(final Request request,
                                        final Map<String, List<String>> headers) {

                for (String headername : request.getHeaderNames()) {

                    Iterable<String> headerValues = request.getHeaders(headername);
                    ArrayList<String> headerValuesCopy = new ArrayList<>();

                    for (String headerValue : headerValues) {

                        headerValuesCopy.add(headerValue);

                    }

                    headers.put(headername, headerValuesCopy);

                }

            }

            private CharArrayWriter getRequestBody(final Request request)
                    throws IOException {

                //
                // Use char array to read chunks instead
                //
                CharArrayWriter requestBuffer = new CharArrayWriter();
                final NIOReader requestReader = request.getNIOReader();

                for (int charRead = requestReader.read(); charRead >= 0; charRead = requestReader.read()) {

                    requestBuffer.write(charRead);

                }

                return requestBuffer;

            }

        });
        this.httpServer.getServerConfiguration().setJmxEnabled(true);
        this.httpServer.start();

    }

    @Override
    public void stopProxy() {

        log.debug("Shutting down HTTP server");
        this.httpServer.shutdownNow();

    }

    private SSLEngineConfigurator createSslConfiguration() {

        //
        // Initialize SSLContext configuration
        //
        SSLContextConfigurator sslContextConfig = new SSLContextConfigurator();
        sslContextConfig.setKeyStoreFile(keystorePath);
        sslContextConfig.setKeyStorePass(keyStorePassword);

        //
        // Create SSLEngine configurator
        //
        return new SSLEngineConfigurator(sslContextConfig.createSSLContext(), false, false, false);

    }


}

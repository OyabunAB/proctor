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
package se.oyabun.proctor.proxy.grizzly;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.oyabun.proctor.proxy.AbstractProctorProxy;
import se.oyabun.proctor.proxy.ProctorProxy;

import java.io.IOException;

/**
 * Proctor Grizzly Proxy
 */
@Component
public class ProctorGrizzlyProxy
        extends AbstractProctorProxy
        implements ProctorProxy {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${se.oyabun.proctor.proxy.listen.port}")
    private int proxyListenPort;

    @Value("${se.oyabun.proctor.proxy.listen.address}")
    private String proxyListenAddress;

    @Value("${se.oyabun.proctor.proxy.local.keystore.path:#{null}}")
    private String keystorePath;

    @Value("${se.oyabun.proctor.proxy.local.keystore.password:#{null}}")
    private String keyStorePassword;

    @Autowired
    private ProctorGrizzlyHttpHandler proctorGrizzlyHttpHandler;

    /**
     * Grizzy HTTP Server instance
     */
    private HttpServer httpServer;

    /**
     * ${@inheritDoc}
     */
    public void startProxy()
            throws
            IOException {

        if (logger.isDebugEnabled()) {

            logger.debug("Starting Proctor Grizzly HTTP Server.");

        }

        this.httpServer = HttpServer.createSimpleServer(null,
                                                        proxyListenPort);

        if (StringUtils.isNotBlank(keystorePath) && StringUtils.isNotBlank(keyStorePassword)) {

            final NetworkListener listener = this.httpServer.getListener("grizzly");
            listener.setSecure(true);
            listener.setSSLEngineConfig(createSslConfiguration());

            if (logger.isDebugEnabled()) {

                logger.debug("Listener: " + listener.getName());

            }

        }

        this.httpServer.getServerConfiguration()
                       .addHttpHandler(proctorGrizzlyHttpHandler);

        this.httpServer.getServerConfiguration()
                       .setJmxEnabled(true);

        this.httpServer.start();

    }

    /**
     * ${@inheritDoc}
     */
    public void stopProxy() {

        if (logger.isDebugEnabled()) {

            logger.debug("Shutting down Proctor Grizzly HTTP Server.");

        }

        this.httpServer.shutdownNow();

    }

    /**
     * Prepare SSLEngine properties
     *
     * @return
     */
    SSLEngineConfigurator createSslConfiguration() {

        //
        // Initialize SSLContext properties
        //
        SSLContextConfigurator sslContextConfig = new SSLContextConfigurator();
        sslContextConfig.setKeyStoreFile(keystorePath);
        sslContextConfig.setKeyStorePass(keyStorePassword);

        //
        // Create SSLEngine configurator
        //
        return new SSLEngineConfigurator(sslContextConfig.createSSLContext(),
                                         false,
                                         false,
                                         false);

    }


}

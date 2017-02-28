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
package se.oyabun.proctor.configuration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.Ssl;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.oyabun.proctor.ProctorServerConfiguration;
import se.oyabun.proctor.handler.properties.ProctorHandlerConfiguration;
import se.oyabun.proctor.handler.staticroute.ProctorStaticRouteConfiguration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;

/**
 * Embedded web application context properties
 */
@Configuration
@EnableAutoConfiguration
public class ProctorApplicationContextConfiguration {

    private int configuredLocalPort;

    @Value("${se.oyabun.proctor.proxy.local.context:/proctoradmin}")
    private String contextPath;

    @Value("${se.oyabun.proctor.proxy.local.port}")
    private int localPort;

    @Value("${se.oyabun.proctor.proxy.listen.port}")
    private int proxyListenPort;

    @Value("${se.oyabun.proctor.proxy.listen.address}")
    private String proxyListenAddress;

    @Value("${se.oyabun.proctor.proxy.local.address}")
    private String localAddress;

    @Value("${se.oyabun.proctor.proxy.local.keystore.path:#{null}}")
    private String keystorePath;

    @Value("${se.oyabun.proctor.proxy.local.keystore.password:#{null}}")
    private String keyStorePassword;

    /**
     * Set up configured ignored uris, will change when auth is completed.
     */
    @Bean
    public static SecurityProperties securityProperties() {

        SecurityProperties securityProperties = new SecurityProperties();
        securityProperties.setIgnored(Arrays.asList("/assets/**",
                                                    "/administration/**",
                                                    "/webjars/**",
                                                    "/index.html"));
        return securityProperties;
    }

    /**
     * Takes care of possible random local ports, so we can skip detecting it on container init callback.
     */
    @PostConstruct
    public void init()
            throws
            IOException {

        ServerSocket serverSocket = new ServerSocket(localPort);

        configuredLocalPort = serverSocket.getLocalPort();

        serverSocket.close();

    }

    /**
     * Produce your servlet container factory
     *
     * @return jetty embedded servlet container factory
     */
    @Bean
    public EmbeddedServletContainerFactory embeddedServletContainerFactory() {

        JettyEmbeddedServletContainerFactory jetty = new JettyEmbeddedServletContainerFactory(configuredLocalPort);

        if (configureSSL()) {

            Ssl ssl = new Ssl();
            ssl.setEnabled(true);
            ssl.setKeyStorePassword(keyStorePassword);
            ssl.setKeyStore(keystorePath);
            jetty.setSsl(ssl);

        }

        jetty.setContextPath(contextPath);

        return jetty;

    }

    boolean configureSSL() {

        return StringUtils.isNotBlank(keystorePath) && StringUtils.isNotBlank(keyStorePassword);

    }

    /**
     * Set up the static route to the administration GUI
     */
    @Bean
    public ProctorHandlerConfiguration staticAdminRouteConfiguration() {

        return new ProctorStaticRouteConfiguration("adminrouteID",
                                                   0,
                                                contextPath + ".*",
                                                   "true",
                                                (configureSSL() ?
                                                    "https" :
                                                    "http") +
                                                "://" + proxyListenAddress + ":" + configuredLocalPort + "/");

    }

    @Bean
    public ProctorServerConfiguration serverConfiguration() {

        return new ProctorServerConfiguration(proxyListenAddress,
                                              localAddress,
                                              contextPath,
                                              configuredLocalPort,
                                              localPort,
                                              proxyListenPort,
                                              keystorePath);

    }

}

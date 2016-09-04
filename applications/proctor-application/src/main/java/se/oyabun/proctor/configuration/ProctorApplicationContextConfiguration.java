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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.Ssl;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.oyabun.proctor.exceptions.DuplicateRouteHandlerException;
import se.oyabun.proctor.handler.ProctorRouteHandler;
import se.oyabun.proctor.handler.manager.ProctorRouteHandlerManager;
import se.oyabun.proctor.handler.staticroute.ProctorStaticRouteProctorRouteHandler;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.Arrays;

/**
 * Embedded web application context configuration
 */
@Configuration
@EnableAutoConfiguration
public class ProctorApplicationContextConfiguration {

    private int configuredLocalPort;

    @Value("${se.oyabun.proctor.proxy.local.port}")
    private int localPort;

    @Value("${se.oyabun.proctor.proxy.listen.address}")
    private String proxyListenAddress;

    @Value("${se.oyabun.proctor.proxy.local.keystore.path:#{null}}")
    private String keystorePath;

    @Value("${se.oyabun.proctor.proxy.local.keystore.password:#{null}}")
    private String keyStorePassword;


    @PostConstruct
    public void init() throws IOException {

        ServerSocket serverSocket = new ServerSocket(localPort);

        configuredLocalPort = serverSocket.getLocalPort();

        serverSocket.close();

    }

    /**
     * Produce your servlet container factory
     * @return jetty embedded servlet container factory
     */
    @Bean
    public EmbeddedServletContainerFactory embeddedServletContainerFactory() {

        Ssl ssl = new Ssl();
        ssl.setEnabled(true);
        ssl.setKeyStorePassword(keyStorePassword);
        ssl.setKeyStore(keystorePath);


        JettyEmbeddedServletContainerFactory jetty = new JettyEmbeddedServletContainerFactory(configuredLocalPort);

        jetty.setSsl(ssl);

        jetty.setContextPath("/proctoradmin");

        return jetty;

    }

    @Bean
    public static SecurityProperties securityProperties() {
        SecurityProperties securityProperties = new SecurityProperties();
        securityProperties.setIgnored(
                Arrays.asList(
                        "/assets/**",
                        "/administration/**",
                        "/webjars/**",
                        "/index.html",
                        "/**"));
        return securityProperties;
    }

    @Autowired @Bean(name="adminWebStaticRoute")
    public ProctorRouteHandler getProctorStaticRouteHandler(
            final ProctorRouteHandlerManager proctorRouteHandlerManager)
            throws MalformedURLException, DuplicateRouteHandlerException {

        ProctorRouteHandler staticRouteHandler =
                new ProctorStaticRouteProctorRouteHandler(
                        "/proctoradmin/.*",
                        "Proctor Admin Web Static Route",
                        new URL(
                                ((StringUtils.isNotBlank(keyStorePassword) &&
                                        StringUtils.isNotBlank(keystorePath)) ?
                                        "https" :
                                        "http") +
                                        "://" + proxyListenAddress + ":" + configuredLocalPort + "/"),
                        true);

        proctorRouteHandlerManager.registerRouteHandler(staticRouteHandler);

        return staticRouteHandler;

    }

    @Autowired @Bean(name = "oyabunWebStaticRoute")
    public ProctorRouteHandler getOyabunStaticRouteHandler(ProctorRouteHandlerManager proctorRouteHandlerManager)
            throws MalformedURLException, DuplicateRouteHandlerException {


        ProctorRouteHandler staticRouteHandlerOyabun =
                new ProctorStaticRouteProctorRouteHandler(
                        "^/((?!proctoradmin).)*$",
                        "Oyabun Route",
                        new URL("https://www.oyabun.se/"),
                        false);

        proctorRouteHandlerManager.registerRouteHandler(staticRouteHandlerOyabun);

        return staticRouteHandlerOyabun;

    }

}

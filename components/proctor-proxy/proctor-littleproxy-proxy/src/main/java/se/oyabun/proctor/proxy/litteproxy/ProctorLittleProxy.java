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
package se.oyabun.proctor.proxy.litteproxy;

import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.oyabun.proctor.proxy.AbstractProctorProxy;
import se.oyabun.proctor.proxy.ProctorProxy;
import se.oyabun.proctor.proxy.litteproxy.filters.ProctorHandlersFilterSource;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Proctor LittleProxy implementation
 */
@Component
public class ProctorLittleProxy
    extends AbstractProctorProxy
        implements ProctorProxy {

    private static final Logger logger = LoggerFactory.getLogger(ProctorProxy.class);

    private HttpProxyServer httpProxyServer;

    @Value("${se.oyabun.proctor.proxy.listen.port}")
    private int proxyListenPort;

    @Value("${se.oyabun.proctor.proxy.listen.address}")
    private String proxyListenAddress;

    @Autowired
    private ProctorHandlersFilterSource proctorHandlersFilterSource;

    /**
     * Init handlers, starting loadbalancer after injections
     * @throws IOException when trying to create proxy server
     */
    public void initHandler() throws IOException {

        logger.info("Initializing Proctor LitteProxy instance on {}:{}.", proxyListenAddress, proxyListenPort);

        httpProxyServer =
                DefaultHttpProxyServer
                        .bootstrap()
                        .withName("Proctor Proxy")
                        .withAddress(new InetSocketAddress(proxyListenAddress, proxyListenPort))
                        .withTransparent(true)
                        .withFiltersSource(proctorHandlersFilterSource)
                        .start();

    }

    public void stopProxy() {

        logger.info("Shutting down Proctor LitteProxy instance.");

        this.httpProxyServer.stop();

    }


}

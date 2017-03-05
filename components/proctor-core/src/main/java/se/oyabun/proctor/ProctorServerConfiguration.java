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
package se.oyabun.proctor;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

public class ProctorServerConfiguration
        implements Serializable {

    public static final String PROTOCOL_SEPARATOR = "://";
    public static final String PORT_SEPARATOR = ":";
    public static final String HTTPS = "https";
    public static final String HTTP = "http";

    private final String nodeID;
    private final String proxyListenAddress;
    private final String localAddress;
    private final String context;
    private final int configuredLocalPort;
    private final int localPort;
    private final int proxyListenPort;
    private final String keystorePath;


    public ProctorServerConfiguration(final String nodeID,
                                      final String proxyListenAddress,
                                      final String localAddress,
                                      final String context,
                                      final int configuredLocalPort,
                                      final int localPort,
                                      final int proxyListenPort,
                                      final String keystorePath) {

        this.nodeID = nodeID;
        this.proxyListenAddress = proxyListenAddress;
        this.localAddress = localAddress;
        this.context = context;
        this.configuredLocalPort = configuredLocalPort;
        this.localPort = localPort;
        this.proxyListenPort = proxyListenPort;
        this.keystorePath = keystorePath;

    }

    public String getLocalAddress() {

        return localAddress;

    }

    public int getProxyListenPort() {

        return proxyListenPort;

    }

    public String getContext() {

        return context;

    }

    public String getProxyListenAddress() {

        return proxyListenAddress;

    }

    public int getConfiguredLocalPort() {

        return configuredLocalPort;

    }

    public int getLocalPort() {

        return localPort;

    }

    public String getKeystorePath() {

        return keystorePath;

    }

    public String getProxyAddressAndPort() {

        return proxyListenAddress + PORT_SEPARATOR + proxyListenPort;

    }

    public String getLocalAddressAndPort() {

        return proxyListenAddress + PORT_SEPARATOR + configuredLocalPort;

    }

    public URL getProxyOriginURL()
            throws MalformedURLException {

        return new URL(getProtocol() + PROTOCOL_SEPARATOR + getProxyAddressAndPort() + "/");

    }

    public String getProtocol() {

        return StringUtils.isNotBlank(keystorePath) ? HTTPS : HTTP;

    }

    public String getLocalContextUrl() {

        return getProtocol() + PROTOCOL_SEPARATOR +
               getProxyListenAddress() + PORT_SEPARATOR +
               getConfiguredLocalPort() + getContext();

    }

    public String getNodeID() {

        return nodeID;

    }

}

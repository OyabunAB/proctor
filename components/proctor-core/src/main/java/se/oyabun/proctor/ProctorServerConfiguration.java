package se.oyabun.proctor;

public class ProctorServerConfiguration {

    private final String proxyListenAddress;
    private final String localAddress;
    private final String context;
    private final int configuredLocalPort;
    private final int localPort;
    private final int proxyListenPort;
    private final String keystorePath;

    public ProctorServerConfiguration(final String proxyListenAddress,
                                      final String localAddress,
                                      final String context,
                                      final int configuredLocalPort,
                                      final int localPort,
                                      final int proxyListenPort,
                                      final String keystorePath) {

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
        return proxyListenAddress + ":" + proxyListenPort;

    }

    public String getLocalAddressAndPort() {

        return proxyListenAddress + ":" + configuredLocalPort;

    }
}

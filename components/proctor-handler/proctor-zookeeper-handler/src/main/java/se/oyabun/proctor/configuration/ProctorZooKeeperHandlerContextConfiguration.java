package se.oyabun.proctor.configuration;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for Proctor Zookeeper Handler
 */
@Configuration
public class ProctorZooKeeperHandlerContextConfiguration {

    private static final int BACKOFF_SLEEP_MS = 5;

    private static final int MAXIMUM_RETRIES = 20;

    @Value("${se.oyabun.proctor.handlers.zookeeper.nodes}")
    private String registryNodesProperty;

    @Value("${se.oyabun.proctor.handlers.zookeeper.basepath}")
    private String basePathProperty;

    @Value("${se.oyabun.proctor.handlers.zookeeper.watch.instances}")
    private boolean whatchInstancesProperty;

    /**
     * Defines the curator framework instance for discovery
     * @return configured curator framework instance
     */
    @Bean(name = "discoveryCuratorFramework")
    public CuratorFramework curatorFramework() {

        //
        // Prefer IPv4 stack for ZooKeeper nodes
        //
        System.setProperty("java.net.preferIPv4Stack", "true");

        CuratorFramework curatorFramework =
                CuratorFrameworkFactory.newClient(
                        registryNodesProperty,
                        new ExponentialBackoffRetry(
                                BACKOFF_SLEEP_MS,
                                MAXIMUM_RETRIES));

        curatorFramework.start();

        return curatorFramework;

    }

    /**
     * Defines the curator service discovery instance for discovery
     * @param curatorFramework autowired
     * @return configured service discovery instance
     */
    @Autowired
    @Bean(name = "discoveryServiceDiscovery",
            initMethod = "start",
            destroyMethod = "close" )
    public ServiceDiscovery serviceDiscovery(
            @Qualifier("discoveryCuratorFramework")
                    CuratorFramework curatorFramework) {

        //
        // Void payload for services
        //
        return ServiceDiscoveryBuilder
                .builder(Void.class)
                .client(curatorFramework)
                .watchInstances(whatchInstancesProperty)
                .basePath(basePathProperty)
                .build();

    }

}

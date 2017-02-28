package se.oyabun.proctor.cluster.manager;

import se.oyabun.proctor.ProctorServerConfiguration;

import java.util.stream.Stream;

public interface ProctorClusterManager {

    /**
     * Get current working server configuration
     *
     * @return current servers configuration item
     */
    ProctorServerConfiguration getThisServer();

    /**
     * Get all cluster nodes server configurations
     *
     * @return stream of all server configurations
     */
    Stream<ProctorServerConfiguration> getServers();

}

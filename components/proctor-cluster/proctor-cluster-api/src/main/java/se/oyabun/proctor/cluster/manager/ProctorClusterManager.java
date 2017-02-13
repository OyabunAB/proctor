package se.oyabun.proctor.cluster.manager;

import se.oyabun.proctor.ProctorServerConfiguration;

import java.util.stream.Stream;

public interface ProctorClusterManager {

    Stream<ProctorServerConfiguration> getServers();

}

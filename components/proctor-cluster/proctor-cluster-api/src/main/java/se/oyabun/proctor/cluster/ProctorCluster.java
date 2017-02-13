package se.oyabun.proctor.cluster;

import se.oyabun.proctor.ProctorServerConfiguration;

import java.util.stream.Stream;

/**
 *
 */
public interface ProctorCluster {

    void registerServer(ProctorServerConfiguration proctorServerConfiguration);

    Stream<ProctorServerConfiguration> getServers();

}

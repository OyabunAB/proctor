package se.oyabun.proctor.cluster.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.oyabun.proctor.ProctorServerConfiguration;
import se.oyabun.proctor.cluster.ProctorCluster;

import java.util.stream.Stream;

@Component
public class DefaultProctorClusterManager
        implements ProctorClusterManager {

    private final ProctorServerConfiguration localProctorServerConfiguration;
    private final ProctorCluster proctorCluster;

    @Autowired
    public DefaultProctorClusterManager(final ProctorServerConfiguration proctorServerConfiguration,
                                        final ProctorCluster proctorCluster) {

        this.localProctorServerConfiguration = proctorServerConfiguration;
        this.proctorCluster = proctorCluster;

        this.proctorCluster.registerServer(proctorServerConfiguration);

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public ProctorServerConfiguration getThisServer() {

        return localProctorServerConfiguration;

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public Stream<ProctorServerConfiguration> getServers() {

        return proctorCluster.getServers();

    }

}

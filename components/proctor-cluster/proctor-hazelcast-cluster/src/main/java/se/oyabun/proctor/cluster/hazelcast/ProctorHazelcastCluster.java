package se.oyabun.proctor.cluster.hazelcast;

import com.hazelcast.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import se.oyabun.proctor.ProctorServerConfiguration;
import se.oyabun.proctor.cluster.ProctorCluster;
import se.oyabun.proctor.events.cluster.ClusterNodeAddedEvent;
import se.oyabun.proctor.events.cluster.ClusterNodeRemovedEvent;

import java.util.stream.Stream;

@Component
public class ProctorHazelcastCluster
        implements ProctorCluster,
                   EntryListener<String, ProctorServerConfiguration> {

    private static final Logger log = LoggerFactory.getLogger(ProctorHazelcastCluster.class);

    public static final String PROCTOR_CLUSTER = "proctorServers";

    private final ApplicationEventPublisher applicationEventPublisher;
    private final HazelcastInstance hazelcastInstance;
    private final ReplicatedMap<String, ProctorServerConfiguration> serverNodes;

    @Autowired
    public ProctorHazelcastCluster(final ApplicationEventPublisher applicationEventPublisher,
                                   final HazelcastInstance hazelcastInstance) {

        this.applicationEventPublisher = applicationEventPublisher;
        this.hazelcastInstance = hazelcastInstance;
        this.serverNodes = hazelcastInstance.getReplicatedMap(PROCTOR_CLUSTER);
        this.serverNodes.addEntryListener(this);

    }

    @Override
    public void registerServer(ProctorServerConfiguration proctorServerConfiguration) {

        if(log.isDebugEnabled()) {

            log.debug("Registering server '{}' in proctor cluster.", proctorServerConfiguration.getProxyAddressAndPort());

        }

        if(!serverNodes.containsKey(proctorServerConfiguration.getProxyAddressAndPort())) {

            serverNodes.put(proctorServerConfiguration.getProxyAddressAndPort(),
                            proctorServerConfiguration);

        }

    }

    @Override
    public Stream<ProctorServerConfiguration> getServers() {

        return serverNodes.values().stream();

    }

    @Override
    public void entryAdded(EntryEvent<String, ProctorServerConfiguration> entryEvent) {

        if(log.isDebugEnabled()) {

            log.debug("Cluster node '{}' added server '{}'.",
                      entryEvent.getMember().getUuid(),
                      entryEvent.getValue().getProxyAddressAndPort());

        }

        applicationEventPublisher.publishEvent(new ClusterNodeAddedEvent<>(entryEvent.getValue()));

    }

    @Override
    public void entryEvicted(EntryEvent<String, ProctorServerConfiguration> entryEvent) {

        if(log.isDebugEnabled()) {

            log.debug("Cluster node '{}' evicted server '{}'.",
                      entryEvent.getMember().getUuid(),
                      entryEvent.getValue().getProxyAddressAndPort());

        }

    }

    @Override
    public void entryRemoved(EntryEvent<String, ProctorServerConfiguration> entryEvent) {

        if(log.isDebugEnabled()) {

            log.debug("Cluster node '{}' removed server '{}'.",
                      entryEvent.getMember().getUuid(),
                      entryEvent.getValue().getProxyAddressAndPort());

        }

        applicationEventPublisher.publishEvent(new ClusterNodeRemovedEvent<>(entryEvent.getValue()));

    }

    @Override
    public void entryUpdated(EntryEvent<String, ProctorServerConfiguration> entryEvent) {

        if(log.isDebugEnabled()) {

            log.debug("Cluster node '{}' updated server '{}'.",
                      entryEvent.getMember().getUuid(),
                      entryEvent.getValue().getProxyAddressAndPort());

        }

    }

    @Override
    public void mapCleared(MapEvent mapEvent) {

        if(log.isDebugEnabled()) {

            log.debug("Cluster node '{}' cleared cluster.",
                      mapEvent.getMember().getUuid());

        }

    }

    @Override
    public void mapEvicted(MapEvent mapEvent) {

        if(log.isDebugEnabled()) {

            log.debug("Cluster node '{}' evicted cluster.",
                      mapEvent.getMember().getUuid());

        }

    }

}

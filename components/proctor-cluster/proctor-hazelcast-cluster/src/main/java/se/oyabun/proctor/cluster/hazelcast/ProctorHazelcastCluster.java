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

            log.debug("Registering server '{}' in proctor cluster.", proctorServerConfiguration.getNodeID());

        }

        if(!serverNodes.containsKey(proctorServerConfiguration.getNodeID())) {

            serverNodes.put(proctorServerConfiguration.getNodeID(),
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
                      entryEvent.getValue().getNodeID());

        }

        applicationEventPublisher.publishEvent(new ClusterNodeAddedEvent<>(entryEvent.getValue()));

    }

    @Override
    public void entryEvicted(EntryEvent<String, ProctorServerConfiguration> entryEvent) {

        if(log.isDebugEnabled()) {

            log.debug("Cluster node '{}' evicted server '{}'.",
                      entryEvent.getMember().getUuid(),
                      entryEvent.getValue().getNodeID());

        }

    }

    @Override
    public void entryRemoved(EntryEvent<String, ProctorServerConfiguration> entryEvent) {

        if(log.isDebugEnabled()) {

            log.debug("Cluster node '{}' removed server '{}'.",
                      entryEvent.getMember().getUuid(),
                      entryEvent.getValue().getNodeID());

        }

        applicationEventPublisher.publishEvent(new ClusterNodeRemovedEvent<>(entryEvent.getValue()));

    }

    @Override
    public void entryUpdated(EntryEvent<String, ProctorServerConfiguration> entryEvent) {

        if(log.isDebugEnabled()) {

            log.debug("Cluster node '{}' updated server '{}'.",
                      entryEvent.getMember().getUuid(),
                      entryEvent.getValue().getNodeID());

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

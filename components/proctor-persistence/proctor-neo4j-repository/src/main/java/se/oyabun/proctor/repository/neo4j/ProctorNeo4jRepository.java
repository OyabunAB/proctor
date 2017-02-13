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
package se.oyabun.proctor.repository.neo4j;

import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.oyabun.proctor.events.ProctorProxyEvent;
import se.oyabun.proctor.handler.properties.ProctorHandlerConfiguration;
import se.oyabun.proctor.persistence.ProctorRepository;

import java.util.Optional;
import java.util.stream.Stream;

@Component
public class ProctorNeo4jRepository
        implements ProctorRepository {

    private static final Logger log = LoggerFactory.getLogger(ProctorNeo4jRepository.class);

    private GraphDatabaseService graphDatabaseService;
    private ProctorNeo4jStore proctorNeo4jStore;

    @Autowired
    public ProctorNeo4jRepository(final GraphDatabaseService graphDatabaseService,
                                  final ProctorNeo4jStore proctorNeo4jStore) {

        this.graphDatabaseService = graphDatabaseService;
        this.proctorNeo4jStore = proctorNeo4jStore;

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public boolean containsConfigurationKey(String configurationID) {

        return getConfiguration(configurationID).isPresent();

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public void persistConfiguration(final ProctorHandlerConfiguration properties) {

        proctorNeo4jStore.createNode(properties.getConfigurationID(),
                                     properties,
                                     graphDatabaseService);

    }



    /**
     * ${@inheritDoc}
     */
    @Override
    public Optional<ProctorHandlerConfiguration> getConfiguration(final String configurationID) {

        return Optional.ofNullable(proctorNeo4jStore.findNode(configurationID,
                                                              graphDatabaseService));

    }





    /**
     * ${@inheritDoc}
     */
    @Override
    public Stream<String> getConfigurationKeys() {

        return getConfigurations()
                .map(ProctorHandlerConfiguration::getConfigurationID);

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public Stream<ProctorHandlerConfiguration> getConfigurations() {

        return getConfigurationKeys().map(this::getConfiguration)
                                     .filter(Optional::isPresent)
                                     .map(Optional::get);

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public void deleteConfiguration(final String configurationID) {

        getConfiguration(configurationID)
                .ifPresent(proctorHandlerProperties ->
                       proctorNeo4jStore.deleteNode(proctorHandlerProperties.getConfigurationID(),
                                                    graphDatabaseService));

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public boolean containsEventKey(String eventID) {

        throw new UnsupportedOperationException();

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public void persistEvent(ProctorProxyEvent proxyEvent) {

        throw new UnsupportedOperationException();

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public Optional<ProctorProxyEvent> getEvent(String eventID) {

        throw new UnsupportedOperationException();

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public Stream<String> getEventKeys() {

        throw new UnsupportedOperationException();

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public Stream<ProctorProxyEvent> getEvents() {

        throw new UnsupportedOperationException();

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public void deleteEvent(final String eventID) {

        throw new UnsupportedOperationException();

    }

}

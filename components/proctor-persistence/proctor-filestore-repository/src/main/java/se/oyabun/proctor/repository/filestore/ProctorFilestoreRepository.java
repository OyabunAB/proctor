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
package se.oyabun.proctor.repository.filestore;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.oyabun.proctor.events.ProctorProxyEvent;
import se.oyabun.proctor.handler.properties.ProctorHandlerConfiguration;
import se.oyabun.proctor.persistence.ProctorRepository;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class ProctorFilestoreRepository
        implements ProctorRepository {

    private static final Logger log = LoggerFactory.getLogger(ProctorFilestoreRepository.class);

    private static final String SERVERS_DIRECTORY = "servers";
    private static final String HANDLERS_DIRECTORY = "handlers";
    private static final String EVENTS_DIRECTORY = "events";

    private String dataDirectoryProperty;
    private ProctorFilestore proctorFilestore;
    private File dataDirectory, handlersDirectory, eventsDirectory, serversDirectory;

    @Autowired
    public ProctorFilestoreRepository(@Value("${se.oyabun.proctor.repository.filestore.directory:./data/filestore}")
                                      final String dataDirectoryProperty,
                                      final ProctorFilestore proctorFilestore)
            throws IOException {

        this.dataDirectoryProperty = dataDirectoryProperty;
        this.proctorFilestore = proctorFilestore;

        dataDirectory = new File(dataDirectoryProperty);
        handlersDirectory = new File(dataDirectory, HANDLERS_DIRECTORY);
        eventsDirectory = new File(dataDirectory, EVENTS_DIRECTORY);
        serversDirectory = new File(dataDirectory, SERVERS_DIRECTORY);

        if(!dataDirectory.exists()) {

            FileUtils.forceMkdir(dataDirectory);

        }

        if(!handlersDirectory.exists()) {

            FileUtils.forceMkdir(handlersDirectory);

        }

        if(!eventsDirectory.exists()) {

            FileUtils.forceMkdir(eventsDirectory);

        }

        if(!serversDirectory.exists()) {

            FileUtils.forceMkdir(serversDirectory);

        }

    }


    /**
     * ${@inheritDoc}
     */
    @Override
    public boolean containsConfigurationKey(final String configurationID) {

        try {

            return FileUtils.directoryContains(handlersDirectory, new File(handlersDirectory, configurationID));

        } catch (IOException e) {

            return false;

        }

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public void persistConfiguration(final ProctorHandlerConfiguration properties) {

        proctorFilestore.createFile(properties.getConfigurationID(),
                                    properties,
                                    handlersDirectory);

    }




    /**
     * ${@inheritDoc}
     */
    @Override
    public Optional<ProctorHandlerConfiguration> getConfiguration(final String configurationID) {

        return Optional.ofNullable(proctorFilestore.readFile(configurationID,
                                                             handlersDirectory));

    }


    /**
     * ${@inheritDoc}
     */
    @Override
    public Stream<String> getConfigurationKeys() {

        return FileUtils.listFiles(handlersDirectory,
                                   null,
                                   false)
                        .stream()
                        .map(File::getName);

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public Stream<ProctorHandlerConfiguration> getConfigurations() {

        return getConfigurationKeys()
                .map(this::getConfiguration)
                .filter(Optional::isPresent)
                .map(Optional::get);

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public void deleteConfiguration(final String configurationID) {

        proctorFilestore.deleteFile(configurationID,
                                    handlersDirectory);

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public boolean containsEventKey(final String eventID) {

        throw new UnsupportedOperationException();

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public void persistEvent(final ProctorProxyEvent proxyEvent) {

        throw new UnsupportedOperationException();

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public Optional<ProctorProxyEvent> getEvent(final String eventID) {

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
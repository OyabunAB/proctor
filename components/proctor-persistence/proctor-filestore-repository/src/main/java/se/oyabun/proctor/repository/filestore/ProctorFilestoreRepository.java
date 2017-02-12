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
import se.oyabun.proctor.handler.properties.ProctorHandlerProperties;
import se.oyabun.proctor.persistence.ProctorRepository;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class ProctorFilestoreRepository
        implements ProctorRepository {

    private static final Logger log = LoggerFactory.getLogger(ProctorFilestoreRepository.class);

    private static final String PROPERTIES_DIRECTORY = "properties";
    private static final String EVENTS_DIRECTORY = "events";

    private String dataDirectoryProperty;
    private ProctorFilestore proctorFilestore;
    private File dataDirectory, propertiesDirectory, eventsDirectory;

    @Autowired
    public ProctorFilestoreRepository(@Value("${se.oyabun.proctor.repository.filestore.directory:./data/filestore}")
                                      final String dataDirectoryProperty,
                                      final ProctorFilestore proctorFilestore)
            throws IOException {

        this.dataDirectoryProperty = dataDirectoryProperty;
        this.proctorFilestore = proctorFilestore;

        dataDirectory = new File(dataDirectoryProperty);
        propertiesDirectory = new File(dataDirectory, PROPERTIES_DIRECTORY);
        eventsDirectory = new File(dataDirectory, EVENTS_DIRECTORY);

        if(!dataDirectory.exists()) {

            FileUtils.forceMkdir(dataDirectory);

        }

        if(!propertiesDirectory.exists()) {

            FileUtils.forceMkdir(propertiesDirectory);

        }

        if(!eventsDirectory.exists()) {

            FileUtils.forceMkdir(eventsDirectory);

        }

    }


    /**
     * ${@inheritDoc}
     */
    @Override
    public boolean containsPropertyKey(final String configurationID) {

        try {

            return FileUtils.directoryContains(propertiesDirectory, new File(configurationID));

        } catch (IOException e) {

            return false;

        }

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public void persistProperty(final ProctorHandlerProperties properties) {

        proctorFilestore.createFile(properties.getConfigurationID(),
                                    properties,
                                    propertiesDirectory);

    }




    /**
     * ${@inheritDoc}
     */
    @Override
    public Optional<ProctorHandlerProperties> getProperty(final String configurationID) {

        return Optional.ofNullable(proctorFilestore.readFile(configurationID,
                                                             propertiesDirectory));

    }


    /**
     * ${@inheritDoc}
     */
    @Override
    public Stream<String> getPropertyKeys() {

        return FileUtils.listFiles(propertiesDirectory,
                                   null,
                                   false)
                        .stream()
                        .map(File::getName);

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public Stream<ProctorHandlerProperties> getProperties() {

        return getPropertyKeys()
                .map(this::getProperty)
                .filter(Optional::isPresent)
                .map(Optional::get);

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public void deleteProperty(final String configurationID) {

        proctorFilestore.deleteFile(configurationID,
                                    propertiesDirectory);

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public boolean containsProxyEventKey(final String eventID) {

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
    public Optional<ProctorProxyEvent> getProxyEvent(final String eventID) {

        throw new UnsupportedOperationException();

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public Stream<String> getProxyEventKeys() {

        throw new UnsupportedOperationException();
    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public Stream<ProctorProxyEvent> getProxyEvents() {

        throw new UnsupportedOperationException();

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public void deleteProxyEvent(final String eventID) {

        throw new UnsupportedOperationException();

    }

}
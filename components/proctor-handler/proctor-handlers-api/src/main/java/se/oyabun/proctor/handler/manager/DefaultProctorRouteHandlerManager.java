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
package se.oyabun.proctor.handler.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import se.oyabun.proctor.handler.ProctorRouteHandler;
import se.oyabun.proctor.handler.properties.ProctorHandlerProperties;
import se.oyabun.proctor.persistence.ProctorRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Default Proctor route handler manager
 */
@Component
public class DefaultProctorRouteHandlerManager
        implements ProctorRouteHandlerManager {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final ApplicationContext applicationContext;
    private final ProctorRepository proctorRepository;

    @Autowired
    public DefaultProctorRouteHandlerManager(final ApplicationContext applicationContext,
                                             final ProctorRepository proctorRepository,
                                             final List<ProctorHandlerProperties> initialProperties) {

        this.applicationContext = applicationContext;
        this.proctorRepository = proctorRepository;

        //
        // Register initial route properties
        //
        initialProperties.stream()
                         .forEach(this::registerRouteProperties);

    }


    /**
     * ${@inheritDoc}
     */
    @Override
    public void registerRouteProperties(final ProctorHandlerProperties properties) {

        if (!proctorRepository.containsPropertyKey(properties.getConfigurationID())) {

            if (log.isDebugEnabled()) {

                log.debug("Registrering new route handler configuration for '{}' with ID '{}'.",
                          properties.getHandlerType(),
                          properties.getConfigurationID());

            }

            proctorRepository.persistProperty(properties);

        } else {

            if (log.isDebugEnabled()) {

                log.debug("Ignoring existing route configuration registration for '{}' with ID '{}'.",
                          properties.getHandlerType(),
                          properties.getConfigurationID());

            }

        }

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public void unregisterRouteProperties(final String configurationID) {

        if (proctorRepository.containsPropertyKey(configurationID)) {

            if (log.isDebugEnabled()) {

                log.debug("Unregistering route handler configuration with ID '{}'.",
                          configurationID);

            }

            proctorRepository.deleteProperty(configurationID);

        } else {

            if (log.isDebugEnabled()) {

                log.debug("Attempted removal of missing route handler configuration with ID '{}'.",
                          configurationID);

            }

        }

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public Stream<ProctorHandlerProperties> getMatchingPropertiesFor(final String input) {

        return proctorRepository.getProperties()
                                .filter(properties -> Pattern.compile(properties.getPattern())
                                                             .matcher(input)
                                                             .matches());

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public Stream<ProctorHandlerProperties> getRegisteredProperties() {

        return proctorRepository.getProperties();

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public Optional<ProctorHandlerProperties> getPropertiesForHandler(final String ID) {

        return proctorRepository.getProperty(ID);

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public Optional<ProctorRouteHandler> getHandler(ProctorHandlerProperties properties) {

        return Arrays.stream(applicationContext.getBeanDefinitionNames())
                     .map(applicationContext::getBean)
                     .filter(object -> object.getClass()
                                             .getName()
                                             .equals(properties.getHandlerType()))
                     .map(object -> (ProctorRouteHandler) object)
                     .findAny();

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public Stream<String> getManagedHandlerTypes() {

        return Arrays.stream(applicationContext.getBeanDefinitionNames())
                     .map(applicationContext::getBean)
                     .map(Object::getClass)
                     .filter(aClass -> aClass.isInstance(ProctorRouteHandler.class))
                     .map(Class::getName);

    }

}

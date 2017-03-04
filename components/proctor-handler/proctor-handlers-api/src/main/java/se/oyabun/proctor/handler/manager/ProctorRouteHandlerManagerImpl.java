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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import se.oyabun.proctor.handler.ProctorRouteHandler;
import se.oyabun.proctor.handler.properties.ProctorHandlerConfiguration;
import se.oyabun.proctor.handler.properties.ProctorRouteConfiguration;
import se.oyabun.proctor.persistence.ProctorRepository;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Default Proctor route handler manager
 */
@Component
public class ProctorRouteHandlerManagerImpl
        implements ProctorRouteHandlerManager {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final ApplicationContext applicationContext;
    private final ProctorRepository proctorRepository;

    private final Map<String, ProctorHandlerConfiguration> localConfigurations = new ConcurrentHashMap<>();

    @Autowired
    public ProctorRouteHandlerManagerImpl(final ApplicationContext applicationContext,
                                          final ProctorRepository proctorRepository,
                                          final List<ProctorRouteConfiguration> initialRouteConfigurations) {

        this.applicationContext = applicationContext;
        this.proctorRepository = proctorRepository;

        //
        // Register initial route properties
        //
        initialRouteConfigurations.stream()
                                  .forEach(this::registerRouteProperties);


    }


    /**
     * ${@inheritDoc}
     */
    @Override
    public void registerRouteProperties(final ProctorHandlerConfiguration properties) {

        if (!proctorRepository.containsConfigurationKey(properties.getConfigurationID())) {

            if (log.isDebugEnabled()) {

                log.debug("Registrering new route handler configuration for '{}' with ID '{}'.",
                          properties.getHandlerType(),
                          properties.getConfigurationID());

            }

            localConfigurations.put(properties.getConfigurationID(), properties);

            proctorRepository.persistConfiguration(properties);

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

        if (proctorRepository.containsConfigurationKey(configurationID) &&
            proctorRepository.getConfiguration(configurationID).isPresent() &&
            !proctorRepository.getConfiguration(configurationID).get().isPersistent()) {

            if (log.isDebugEnabled()) {

                log.debug("Unregistering route handler configuration with ID '{}'.",
                          configurationID);

            }

            localConfigurations.remove(configurationID);

            proctorRepository.deleteConfiguration(configurationID);

        } else {

            if (log.isDebugEnabled()) {

                log.debug("Not removing route handler configuration with ID '{}'.",
                          configurationID);

            }

        }

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public Optional<ProctorHandlerConfiguration> getMatchingPropertiesFor(final String input) {

        return proctorRepository.getConfigurations()
                                .filter(properties -> Pattern.compile(properties.getPattern())
                                                             .matcher(input)
                                                             .matches())
                                .findFirst();

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public Stream<ProctorHandlerConfiguration> getRegisteredProperties() {

        return proctorRepository.getConfigurations();

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public Optional<ProctorHandlerConfiguration> getPropertiesForHandler(final String ID) {

        return proctorRepository.getConfiguration(ID);

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public Optional<ProctorRouteHandler> getHandler(ProctorHandlerConfiguration properties) {

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
                     .filter(aClass ->
                                     Arrays.asList(aClass.getInterfaces())
                                           .contains(ProctorRouteHandler.class))
                     .map(Class::getName);

    }

    @PreDestroy
    public void unregisterRoutes() {

        if(log.isDebugEnabled()) {

            log.debug("Unregistering local routes '{}'.",
                      StringUtils.join(localConfigurations.keySet(), ","));

        }

        localConfigurations.keySet().forEach(this::unregisterRouteProperties);

    }

}

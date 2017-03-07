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
package se.oyabun.proctor.web.admin.api.handlers.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import se.oyabun.proctor.handler.manager.ProctorRouteHandlerManager;
import se.oyabun.proctor.handler.properties.ProctorHandlerConfiguration;

import java.util.Arrays;
import java.util.Optional;

/**
 * Proctor Handlers REST API
 *
 * @version 1
 */
@RestController
@RequestMapping(value = HandlersRestController.HANDLERS_ROOT)
public class HandlersRestController {

    public static final String HANDLERS_ROOT = "/api/v1/handlers";
    public static final String CONFIGURATIONS = "/configurations";
    public static final String HANDLER_TYPES = "/handlertypes";

    public static final String HANDLERTYPE_PROPERTY = "handlerType";
    public static final String CONFIGURATIONID_PROPERTY = "configurationID";

    private ProctorRouteHandlerManager proctorRouteHandlerManager;

    @Autowired
    public HandlersRestController(final ProctorRouteHandlerManager proctorRouteHandlerManager) {

        this.proctorRouteHandlerManager = proctorRouteHandlerManager;

    }

    /**
     * Get all current registered configurations
     *
     * @return all handler configurations in use
     */
    @RequestMapping(value = HandlersRestController.CONFIGURATIONS,
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String[]> getConfigurations() {

        final String[] registeredProctorRouteHandlers =
                proctorRouteHandlerManager.getRegisteredProperties()
                                          .map(ProctorHandlerConfiguration::getConfigurationID)
                                          .toArray(size -> new String[size]);

        return !Arrays.asList(registeredProctorRouteHandlers).isEmpty() ?
               ResponseEntity.status(HttpStatus.OK).body(registeredProctorRouteHandlers) :
               ResponseEntity.status(HttpStatus.NO_CONTENT).body(new String[0]);

    }

    /**
     * Returns all valid managed handler types
     *
     * @param handlerType of configuration request
     * @throws ClassNotFoundException on failure to find class in confing
     * @return all handler configurations for given handler type
     */
    @RequestMapping(value = HandlersRestController.HANDLER_TYPES +
                            "/{" + HandlersRestController.HANDLERTYPE_PROPERTY + "}" +
                            HandlersRestController.CONFIGURATIONS,
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProctorHandlerConfiguration[]> getForHandlerType(@PathVariable(HANDLERTYPE_PROPERTY)
                                                                           final String handlerType)
            throws ClassNotFoundException {

        final ProctorHandlerConfiguration[] propertiesForType =
                proctorRouteHandlerManager.getRegisteredProperties()
                                          .filter(properties -> properties.getHandlerType().equals(handlerType))
                                          .toArray(size -> new ProctorHandlerConfiguration[size]);

        return !Arrays.asList(propertiesForType).isEmpty() ?
               ResponseEntity.status(HttpStatus.OK).body(propertiesForType) :
               ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ProctorHandlerConfiguration[0]);

    }

    /**
     * Returns all available handler types
     *
     * @return all handler types
     */
    @RequestMapping(value = HandlersRestController.HANDLER_TYPES,
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String[]> getHandlerTypes() {

        final String[] managedHandlerTypes =
                proctorRouteHandlerManager.getManagedHandlerTypes()
                                          .toArray(size -> new String[size]);

        return !Arrays.asList(managedHandlerTypes).isEmpty() ?
               ResponseEntity.status(HttpStatus.OK).body(managedHandlerTypes) :
               ResponseEntity.status(HttpStatus.NO_CONTENT).body(new String[0]);

    }

    /**
     * Returns a specific configuration
     *
     * @param configurationID to be returned
     *
     * @return handler configuration with specified configuration ID
     */
    @RequestMapping(value = HandlersRestController.CONFIGURATIONS +
                            "/{" + HandlersRestController.CONFIGURATIONID_PROPERTY + "}",
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProctorHandlerConfiguration> getConfiguration(@PathVariable(CONFIGURATIONID_PROPERTY)
                                                                        final String configurationID) {

        final Optional<ProctorHandlerConfiguration> optionalProperty =
                proctorRouteHandlerManager.getPropertiesForHandler(configurationID);

        return optionalProperty.isPresent() ?
               ResponseEntity.status(HttpStatus.OK).body(optionalProperty.get()) :
               ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);

    }

    /**
     * Creates a new handler configuration
     *
     * @param handlerConfiguration to be created
     */
    @RequestMapping(value = HandlersRestController.CONFIGURATIONS,
                    method = RequestMethod.POST,
                    consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> createConfiguration(@RequestBody
                                                    final ProctorHandlerConfiguration handlerConfiguration) {

        proctorRouteHandlerManager.registerRouteProperties(handlerConfiguration);

        return ResponseEntity.status(HttpStatus.CREATED).body(null);

    }

    /**
     * Unregisters and removes a handler configuration
     *
     * @param configurationID of the configuration to remove
     */
    @RequestMapping(value = HandlersRestController.CONFIGURATIONS +
                            "/{" + HandlersRestController.CONFIGURATIONID_PROPERTY + "}/",
                    method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteConfiguration(@PathVariable(CONFIGURATIONID_PROPERTY)
                                                    final String configurationID) {

        proctorRouteHandlerManager.unregisterRouteProperties(configurationID);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);

    }

}

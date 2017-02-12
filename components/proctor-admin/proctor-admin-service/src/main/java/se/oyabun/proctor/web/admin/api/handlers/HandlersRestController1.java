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
package se.oyabun.proctor.web.admin.api.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.oyabun.proctor.handler.manager.ProctorRouteHandlerManager;
import se.oyabun.proctor.handler.properties.ProctorHandlerProperties;
import se.oyabun.proctor.web.admin.api.AbstractSecuredAPIController;

import java.util.Arrays;
import java.util.Optional;

/**
 * Proctor Handlers REST API
 *
 * @version 1
 */
@RestController
@RequestMapping(value = HandlersRestController1.API_ROOT)
public class HandlersRestController1
        extends AbstractSecuredAPIController {

    public static final String API_ROOT = "/api/1";
    public static final String CONFIGURATIONS = "/configurations/";
    public static final String CONFIGURATIONID_PROPERTY = "{configurationID}";
    public static final String HANDLER_TYPES = "/handletypes";

    private ProctorRouteHandlerManager proctorRouteHandlerManager;

    @Autowired
    public HandlersRestController1(final ProctorRouteHandlerManager proctorRouteHandlerManager) {

        this.proctorRouteHandlerManager = proctorRouteHandlerManager;

    }

    /**
     * Get all current registered configurations
     *
     * @return all handler configurations in use
     */
    @RequestMapping(value = HandlersRestController1.CONFIGURATIONS,
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ProctorHandlerProperties[]> getConfigurations() {

        final ProctorHandlerProperties[] registeredProctorRouteHandlers =
                proctorRouteHandlerManager.getRegisteredProperties()
                                          .toArray(size -> new ProctorHandlerProperties[size]);

        return !Arrays.asList(registeredProctorRouteHandlers).isEmpty() ?
               ResponseEntity.status(HttpStatus.OK).body(registeredProctorRouteHandlers) :
               ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ProctorHandlerProperties[0]);

    }

    /**
     * Returns all valid managed handler types
     *
     * @return all handler type names valid for configurations
     */
    @RequestMapping(value = HandlersRestController1.HANDLER_TYPES,
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String[]> getTypes() {

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
     * @return handler configuration with specified configuration ID
     */
    @RequestMapping(value = HandlersRestController1.CONFIGURATIONS +
                            HandlersRestController1.CONFIGURATIONID_PROPERTY,
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ProctorHandlerProperties> getConfiguration(@PathVariable
                                                                     final String configurationID) {

        final Optional<ProctorHandlerProperties> optionalProperty =
                proctorRouteHandlerManager.getPropertiesForHandler(configurationID);

        return optionalProperty.isPresent() ?
               ResponseEntity.status(HttpStatus.OK).body(optionalProperty.get()) :
               ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);

    }

    /**
     * Creates a new handler configuration
     *
     * @param proctorHandlerProperties to be created
     */
    @RequestMapping(value = HandlersRestController1.CONFIGURATIONS,
                    method = RequestMethod.POST,
                    consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createConfiguration(@RequestBody
                       ProctorHandlerProperties proctorHandlerProperties) {

        proctorRouteHandlerManager.registerRouteProperties(proctorHandlerProperties);

    }

    /**
     * Unregisters and removes a handler configuration
     *
     * @param configurationID of the configuration to remove
     */
    @RequestMapping(value = HandlersRestController1.CONFIGURATIONS +
                            HandlersRestController1.CONFIGURATIONID_PROPERTY,
                    method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteConfiguration(@PathVariable
                                    final String configurationID) {

        proctorRouteHandlerManager.unregisterRouteProperties(configurationID);

    }

}

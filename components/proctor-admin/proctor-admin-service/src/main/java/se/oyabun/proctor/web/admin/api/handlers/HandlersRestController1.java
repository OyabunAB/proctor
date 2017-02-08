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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import se.oyabun.proctor.handler.manager.ProctorRouteHandlerManager;
import se.oyabun.proctor.handler.properties.ProctorHandlerProperties;
import se.oyabun.proctor.web.admin.api.AbstractSecuredAPIController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Proctor Handlers REST API
 * @version 1
 */
@RequestMapping(value = "/api/1/handlers")
public class HandlersRestController1
        extends AbstractSecuredAPIController {

    @Autowired
    private ProctorRouteHandlerManager proctorRouteHandlerManager;

    @RequestMapping(
            value = "/",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ProctorHandlerProperties[]> getAll() {

        final List<ProctorHandlerProperties> registeredProctorRouteHandlers =
                proctorRouteHandlerManager.getRegisteredProperties().collect(Collectors.toList());

        if(registeredProctorRouteHandlers.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(new ProctorHandlerProperties[]{});

        } else {

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(registeredProctorRouteHandlers
                            .stream()
                            .toArray(size -> new ProctorHandlerProperties[size]));

        }

    }

    @RequestMapping(
            value = "/{ID}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ProctorHandlerProperties> get(@PathVariable("ID")
                                                        final String ID) {

        final Optional<ProctorHandlerProperties> optionalProperty =
                proctorRouteHandlerManager.getProperty(ID);

        if(!optionalProperty.isPresent()) {

            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(null);

        } else {

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(optionalProperty.get());

        }

    }


}

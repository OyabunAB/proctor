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
import se.oyabun.proctor.handler.ProctorRouteHandler;
import se.oyabun.proctor.handler.manager.ProctorRouteHandlerManager;
import se.oyabun.proctor.web.admin.api.AbstractSecuredAPIController;

import java.util.Collection;
import java.util.Optional;

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
    public ResponseEntity<ProctorRouteHandler[]> getAllRegisteredRouteHandlers() {

        final Collection<ProctorRouteHandler> registeredProctorRouteHandlers =
                proctorRouteHandlerManager.getRegisteredRouteHandlers();

        if(proctorRouteHandlerManager.getRegisteredRouteHandlers().isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(new ProctorRouteHandler[]{});

        } else {

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(registeredProctorRouteHandlers.toArray(
                            new ProctorRouteHandler[registeredProctorRouteHandlers.size()]));

        }

    }

    @RequestMapping(
            value = "/{handlerName}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ProctorRouteHandler> getSpecificRouteHandlerFor(@PathVariable("handlerName")
                                                                          final String handlerName) {

        final Collection<ProctorRouteHandler> registeredProctorRouteHandlers =
                proctorRouteHandlerManager.getRegisteredRouteHandlers();

        if(registeredProctorRouteHandlers.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(null);

        } else {

            Optional<ProctorRouteHandler> optionalRouteHandler =
                    registeredProctorRouteHandlers
                            .stream()
                            .filter(proctorRouteHandler ->
                                    proctorRouteHandler.getHandleNames().contains(handlerName))
                            .findFirst();

            if(optionalRouteHandler.isPresent()) {

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(optionalRouteHandler.get());

            } else {

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(null);

            }

        }

    }


}

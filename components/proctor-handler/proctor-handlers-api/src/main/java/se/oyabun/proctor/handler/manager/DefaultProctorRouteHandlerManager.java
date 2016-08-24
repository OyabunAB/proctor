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
import org.springframework.stereotype.Component;
import se.oyabun.proctor.exceptions.DuplicateRouteHandlerException;
import se.oyabun.proctor.exceptions.NoSuchHandlerException;
import se.oyabun.proctor.handler.ProctorRouteHandler;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Default proctor route handler manager
 */
@Component
public class DefaultProctorRouteHandlerManager
        implements ProctorRouteHandlerManager {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final ConcurrentMap<UUID, ProctorRouteHandler> routeHandlerMap = new ConcurrentHashMap<>();

    private static final Object syncronizationLock = new Object(){};

    /**
     * ${@inheritDoc}
     */
    public void registerRouteHandler(ProctorRouteHandler proctorRouteHandler)
            throws DuplicateRouteHandlerException {

        synchronized (syncronizationLock) {

            if(!routeHandlerMap.containsValue(proctorRouteHandler)) {

                final UUID registrationUUID = UUID.randomUUID();

                if(log.isDebugEnabled()) {

                    for(String handlerName : proctorRouteHandler.getHandleNames()) {

                        log.debug("Registrering new route handler '{}' with ID '{}'.",
                                handlerName,
                                registrationUUID.toString());

                    }

                }

                routeHandlerMap.put(registrationUUID, proctorRouteHandler);

            } else {

                throw new DuplicateRouteHandlerException("Handler already registered with handler.");

            }

        }

    }

    /**
     * ${@inheritDoc}
     */
    public void unregisterRouteHandler(ProctorRouteHandler proctorRouteHandler)
            throws NoSuchHandlerException {

        synchronized (syncronizationLock) {

            if(routeHandlerMap.containsValue(proctorRouteHandler)) {

                Optional<UUID> optionalKey =
                        routeHandlerMap.keySet()
                                .stream()
                                .filter(uuid -> routeHandlerMap.get(uuid).equals(proctorRouteHandler))
                                .findFirst();

                if(optionalKey.isPresent()) {

                    final UUID foundUUID = optionalKey.get();

                    if(log.isDebugEnabled()) {

                        for(String handlerName : proctorRouteHandler.getHandleNames()) {

                            log.debug("Unregistering route handler '{}' with ID '{}'.",
                                    handlerName,
                                    foundUUID.toString());

                        }

                    }

                    routeHandlerMap.remove(foundUUID);

                }

            } else {

                if(log.isDebugEnabled()) {

                    log.debug("Attempted unregistering of route handler failed.");

                }

                throw new NoSuchHandlerException("Handler not registered in manager.");

            }

        }

    }

    /**
     * ${@inheritDoc}
     */
    public Set<ProctorRouteHandler> getRegisteredRouteHandlers() {

        synchronized (syncronizationLock) {

            return routeHandlerMap
                    .values()
                    .stream()
                    .collect(Collectors.toSet());

        }

    }

}

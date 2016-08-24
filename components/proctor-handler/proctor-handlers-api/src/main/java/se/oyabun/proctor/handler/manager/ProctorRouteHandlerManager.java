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

import se.oyabun.proctor.exceptions.DuplicateRouteHandlerException;
import se.oyabun.proctor.exceptions.NoSuchHandlerException;
import se.oyabun.proctor.handler.ProctorRouteHandler;

import java.util.Set;

/**
 * Proctor route handler manager, for managing route handler on the fly
 */
public interface ProctorRouteHandlerManager {

    /**
     * Register a route handler with a manager
     * @param proctorRouteHandler to register
     * @throws DuplicateRouteHandlerException if duplicate handler is already registered
     */
    void registerRouteHandler(final ProctorRouteHandler proctorRouteHandler)
        throws DuplicateRouteHandlerException;

    /**
     * Unregister a route handler with a manager
     * @param proctorRouteHandler to unregister
     * @throws NoSuchHandlerException if given handler is not registered with handler manager
     */
    void unregisterRouteHandler(final ProctorRouteHandler proctorRouteHandler)
        throws NoSuchHandlerException;

    /**
     * Return all registered handler
     * @return set of route handler
     */
    Set<ProctorRouteHandler> getRegisteredRouteHandlers();

}

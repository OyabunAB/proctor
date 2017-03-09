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

import se.oyabun.proctor.handler.ProctorRouteHandler;
import se.oyabun.proctor.handler.properties.ProctorHandlerConfiguration;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Proctor route handler manager, for managing route handler on the fly
 */
public interface ProctorRouteHandlerManager {

    /**
     * Register a route handler with a manager
     *
     * @param properties to register
     */
    void registerRouteProperties(final ProctorHandlerConfiguration properties);

    /**
     * Unregister a route handler with a manager
     *
     * @param configurationID of property to unregister
     */
    void unregisterRouteProperties(final String configurationID);

    /**
     * Get all matching handlers for input
     *
     * @param input to match properties on
     * @return optional matching properties
     */
    Optional<ProctorHandlerConfiguration> getMatchingPropertiesFor(final String input);

    /**
     * Return all registered handler properties
     *
     * @return stream of all route handler properties
     */
    Stream<ProctorHandlerConfiguration> getRegisteredProperties();

    /**
     * Return specified property for ID
     *
     * @param configurationID of property
     * @return optional property
     */
    Optional<ProctorHandlerConfiguration> getPropertiesForHandler(final String configurationID);

    /**
     * Return specific handler for property
     *
     * @param properties to find handler for
     * @return optional handler
     */
    Optional<ProctorRouteHandler> getHandler(final ProctorHandlerConfiguration properties);

    /**
     * Returns stream of handler types that is manageable
     *
     * @return stream of proctor route handler type names
     */
    Stream<String> getManagedHandlerTypes();

}

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
package se.oyabun.proctor.handler.staticroute;

import se.oyabun.proctor.handler.properties.ProctorHandlerProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * The proctor route properties is used for determining
 * handler configuration and type prior to handle a proxy request.
 *
 * @see se.oyabun.proctor.handler.ProctorRouteHandler
 * @see se.oyabun.proctor.handler.manager.ProctorRouteHandlerManager
 */
public class ProctorStaticRouteProperties
        implements ProctorHandlerProperties {

    private String configurationID;
    private int priority;
    private String pattern;
    private Map<String, String> properties = new HashMap<>();

    public ProctorStaticRouteProperties(final String configurationID,
                                        final int priority,
                                        final String pattern,
                                        final String appendPath,
                                        final String defaultUrl) {

        this.configurationID = configurationID;
        this.priority = priority;
        this.pattern = pattern;
        this.properties.put(ProctorStaticRouteHandler.APPEND_PATH_PROPERTY,
                            appendPath);
        this.properties.put(ProctorStaticRouteHandler.DEFAULT_URL_PROPERTY,
                            defaultUrl);

    }

    /**
     * Gets unique configuration ID representing property
     *
     * @return configuration ID
     */
    @Override
    public String getConfigurationID() {

        return configurationID;
    }

    /**
     * Gets configured priority for sorting precedence on multiple matching properties
     *
     * @return priority of instance
     */
    @Override
    public int getPriority() {

        return priority;
    }

    /**
     * Handler type is indicated by the simple name of the handler class.
     * Used for resolving handlers.
     *
     * @return handler type
     */
    @Override
    public String getHandlerType() {

        return ProctorStaticRouteHandler.class.getName();
    }

    /**
     * Pattern for matching this configuration on incoming request strings
     *
     * @return regexp pattern
     */
    @Override
    public String getPattern() {

        return pattern;
    }

    /**
     * Specific handler property map holding specific properties used by handler type
     *
     * @return string properties for configuring handler for the request
     */
    @Override
    public Map<String, String> getProperties() {

        return properties;
    }

}

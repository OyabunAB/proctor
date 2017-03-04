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
package se.oyabun.proctor.handler.properties;

import java.util.HashMap;
import java.util.Map;

/**
 * Base proctor route configuration
 */
public class ProctorRouteConfiguration
        implements ProctorHandlerConfiguration{

    private String configurationID;
    private int priority;
    private String pattern;
    private String handlerType;
    private boolean persistent;
    private Map<String, String> properties = new HashMap<>();

    public ProctorRouteConfiguration(final String configurationID,
                                     final int priority,
                                     final String pattern,
                                     final String routeType,
                                     final boolean persistent,
                                     final Map<String, String> properties) {

        this.configurationID = configurationID;
        this.priority = priority;
        this.pattern = pattern;
        this.handlerType = routeType;
        this.persistent = persistent;
        this.properties = properties;

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public String getConfigurationID() {

        return configurationID;
    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public int getPriority() {

        return priority;
    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public String getHandlerType() {

        return handlerType;

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public String getPattern() {

        return pattern;
    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public Map<String, String> getProperties() {

        return properties;
    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public boolean isPersistent() {

        return persistent;
    }

}

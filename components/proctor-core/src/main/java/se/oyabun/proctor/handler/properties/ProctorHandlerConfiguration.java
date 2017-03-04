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

import java.io.Serializable;
import java.util.Map;

/**
 * Handler properties representation,
 * implementations of which is used to identify and configure a handler instance.
 */
public interface ProctorHandlerConfiguration
        extends Serializable {

    String CACHE_NAME = "ProctorHandlerConfiguration";
    String CACHE_KEY = "configurationID";

    String APPEND_PATH_PROPERTY = "appendpath";
    String APPEND_MATCHER_GROUP = "appendmatchergroup";
    String DEFAULT_URL_PROPERTY = "defaulturl";

    /**
     * Cluster/global unique handler identification string
     *
     * @return identification string for handler instance
     */
    String getConfigurationID();

    /**
     * Priority value for handler properties, lower to higher will be selected
     *
     * @return handler instance priority, used for ordering of handler
     */
    int getPriority();

    /**
     * Handler type information, used to find specific handler type, e.g.
     * specific handler interface/class to instantiate
     *
     * @return name of handler type
     */
    String getHandlerType();

    /**
     * Handler matcher pattern
     *
     * @return pattern for matching requests
     */
    String getPattern();

    /**
     * Handler interpreted property entries for configuration
     */
    Map<String, String> getProperties();

    /**
     * Handler should persist between shutdowns
     * @return
     */
    boolean isPersistent();

}

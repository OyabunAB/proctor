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

import com.google.common.collect.ImmutableMap;
import se.oyabun.proctor.handler.properties.ProctorHandlerConfiguration;
import se.oyabun.proctor.handler.properties.ProctorRouteConfiguration;

import java.util.Map;

/**
 * The proctor route properties is used for determining
 * handler configuration and type prior to handle a proxy request.
 *
 * @see se.oyabun.proctor.handler.ProctorRouteHandler
 * @see se.oyabun.proctor.handler.manager.ProctorRouteHandlerManager
 */
public class ProctorStaticRouteConfiguration
        implements ProctorHandlerConfiguration {

    private ProctorRouteConfiguration proctorRouteConfiguration;

    public ProctorStaticRouteConfiguration(final String configurationID,
                                           final int priority,
                                           final String pattern,
                                           final boolean persistent,
                                           final String appendPath,
                                           final String defaultUrl) {

        proctorRouteConfiguration =
                new ProctorRouteConfiguration(configurationID,
                                              priority,
                                              pattern,
                                              ProctorStaticRouteConfiguration.class.getName(),
                                              persistent,
                                              ImmutableMap.of(APPEND_PATH_PROPERTY, appendPath,
                                                              DEFAULT_URL_PROPERTY, defaultUrl));

    }

    /**
     * ${@inheritDoc}
     */
    @Override
    public String getConfigurationID() {

        return proctorRouteConfiguration.getConfigurationID();
    }

    @Override
    public int getPriority() {

        return proctorRouteConfiguration.getPriority();
    }

    @Override
    public String getHandlerType() {

        return proctorRouteConfiguration.getHandlerType();
    }

    @Override
    public String getPattern() {

        return proctorRouteConfiguration.getPattern();
    }

    @Override
    public Map<String, String> getProperties() {

        return proctorRouteConfiguration.getProperties();

    }

    @Override
    public boolean isPersistent() {

        return proctorRouteConfiguration.isPersistent();

    }
}

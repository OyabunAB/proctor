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

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.stereotype.Component;
import se.oyabun.proctor.exceptions.NoHandleForNameException;
import se.oyabun.proctor.handler.AbstractDefaultProctorRouteHandler;
import se.oyabun.proctor.handler.ProctorRouteHandler;
import se.oyabun.proctor.handler.properties.ProctorHandlerConfiguration;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Proctor Static Route handler implementation
 */
@Component
public class ProctorStaticRouteHandler
        extends AbstractDefaultProctorRouteHandler
        implements ProctorRouteHandler {

    public static final String APPEND_PATH_PROPERTY = "appendpath";
    public static final String DEFAULT_URL_PROPERTY = "defaulturl";

    /**
     * Return staticly configured URL for configured matcher pattern.
     *
     * @param input to request on base URL
     * @return static configured url
     * @throws NoHandleForNameException when no handle can be found for given name
     */
    public URL resolveURLFor(final String input,
                             final ProctorHandlerConfiguration properties)
            throws
            NoHandleForNameException,
            MalformedURLException {

        boolean appendOriginalPath = Boolean.valueOf(properties.getProperties()
                                                               .getOrDefault(APPEND_PATH_PROPERTY,
                                                                             "false"));

        return appendOriginalPath ?
               new URL(getRoot(properties),
                       input) :
               getRoot(properties);

    }

    /**
     * Returns the configured static root URL
     *
     * @param properties for static handler
     * @return the configured root url
     */
    @Override
    protected URL getRoot(final ProctorHandlerConfiguration properties)
            throws
            MalformedURLException {

        return new URL(properties.getProperties()
                                 .getOrDefault(DEFAULT_URL_PROPERTY,
                                               "/"));

    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17,
                                   37).append(this)
                                      .toHashCode();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof ProctorStaticRouteHandler)) {
            return false;
        }

        ProctorStaticRouteHandler that = (ProctorStaticRouteHandler) o;

        return new org.apache.commons.lang3.builder.EqualsBuilder().append(this,
                                                                           that)
                                                                   .isEquals();
    }

}

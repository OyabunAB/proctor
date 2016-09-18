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
import se.oyabun.proctor.exceptions.NoHandleForNameException;
import se.oyabun.proctor.handler.AbstractDefaultProctorRouteHandler;
import se.oyabun.proctor.handler.ProctorRouteHandler;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Proctor Static Route handler implementation
 */
public class ProctorStaticRouteProctorRouteHandler
    extends AbstractDefaultProctorRouteHandler
        implements ProctorRouteHandler {

    private final URL staticURL;

    private final boolean appendOriginalPath;

    public ProctorStaticRouteProctorRouteHandler(final String regex,
                                                 final String matcherHandle,
                                                 final URL staticURL,
                                                 final boolean appendOriginalPath) {

        super(regex, matcherHandle);

        this.staticURL = staticURL;
        this.appendOriginalPath = appendOriginalPath;

    }

    /**
     * Return staticly configured URL for configured matcher pattern.
     * @param handleName to find URL for
     * @param uri to request on base URL
     * @return static configured url
     * @throws NoHandleForNameException when no handle can be found for given name
     */
    public URL resolveURLFor(final String handleName,
                             final String uri)
            throws NoHandleForNameException, MalformedURLException {

        //
        // Default handler only has one handle name
        //
        assert getHandleNames().size() == 1;

        final String singleHandleName = getHandleNames().iterator().next();

        if(singleHandleName.equals(handleName)) {

            return appendOriginalPath ?
                    new URL(getRootURLForHandleName(singleHandleName), uri) :
                    getRootURLForHandleName(singleHandleName);

        } else {

            throw new NoHandleForNameException(
                    String.format("No handle for given handle name '%s'.", handleName));

        }

    }

    /**
     * Returns the configured static root URL
     * @param handleName to get root url for
     * @return the configured root url
     */
    protected URL getRootURLForHandleName(String handleName) {

        return staticURL;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ProctorStaticRouteProctorRouteHandler)) return false;

        ProctorStaticRouteProctorRouteHandler that = (ProctorStaticRouteProctorRouteHandler) o;

        return new org.apache.commons.lang3.builder.EqualsBuilder()
                .append(getHandleNames(), that.getHandleNames())
                .append(appendOriginalPath, that.appendOriginalPath)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getHandleNames())
                .append(appendOriginalPath)
                .toHashCode();
    }

}

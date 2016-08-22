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
package se.oyabun.proctor.handlers.staticroute;

import org.springframework.stereotype.Component;
import se.oyabun.proctor.exceptions.NoHandleForNameException;
import se.oyabun.proctor.handlers.AbstractDefaultProctorRouteHandler;
import se.oyabun.proctor.handlers.ProctorRouteHandler;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Proctor Filesystem handlers
 */
@Component
public class ProctorStaticRouteProctorRouteHandler
    extends AbstractDefaultProctorRouteHandler
        implements ProctorRouteHandler {

    private static URL staticURL;

    public ProctorStaticRouteProctorRouteHandler(final String regex,
                                                 final String matcherHandle,
                                                 final URL staticURL) {

        super(regex, matcherHandle);

        this.staticURL = staticURL;

    }

    /**
     * Return staticly configured URL for configured matcher pattern.
     * @param handleName to find URL for
     * @param uri to request on base URL
     * @return static configured url
     * @throws NoHandleForNameException
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

            return new URL(getRootURLForHandleName(singleHandleName), uri);

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

}

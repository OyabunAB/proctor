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
package se.oyabun.proctor.handler;

import se.oyabun.proctor.exceptions.NoHandleForNameException;
import se.oyabun.proctor.handler.properties.ProctorHandlerConfiguration;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Default abstract handler implementation
 */
public abstract class AbstractDefaultProctorRouteHandler
        implements ProctorRouteHandler {

    /**
     * Force implementation of URL resolver.
     *
     * @param input to match
     * @return complete URL including requested input
     */
    public abstract URL resolveURLFor(final String input,
                                      final ProctorHandlerConfiguration properties)
            throws
            NoHandleForNameException,
            MalformedURLException;

    /**
     * Return root URL for given handle name
     *
     * @param properties for handler
     * @return root for handler URL
     * @throws MalformedURLException if root url cant be created
     */
    protected abstract URL getRoot(final ProctorHandlerConfiguration properties)
            throws
            MalformedURLException;

}

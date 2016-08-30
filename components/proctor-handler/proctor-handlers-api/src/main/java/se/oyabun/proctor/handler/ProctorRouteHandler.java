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

import se.oyabun.proctor.exceptions.InputNotMatchedException;
import se.oyabun.proctor.exceptions.NoHandleForNameException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

/**
 * ProctorRouteHandler interface.
 * All proctor handler must implement this.
 */
public interface ProctorRouteHandler {

    /**
     * Does given input match this handler?
     * @param uri to verify
     * @return true if the handler can handle the given URI
     */
    boolean matches(final String uri);


    /**
     * Get handle name for given inpu
     * @param uri to get handle for
     * @return handle name to use for URL resolving
     * @throws InputNotMatchedException if input does not match
     */
    String getHandleNameFor(String uri)
            throws InputNotMatchedException;

    /**
     * Get all handle names in handler.
     * @return set of unique handle names
     */
    Set<String> getHandleNames();

    /**
     * Return URL for given handle name
     * @param handleName to resolve URL for
     * @param uri of request
     * @return complete url including requested uri for given handle name
     * @throws NoHandleForNameException if handle is not present
     */
    URL resolveURLFor(final String handleName,
                      final String uri)
            throws NoHandleForNameException, MalformedURLException;

}

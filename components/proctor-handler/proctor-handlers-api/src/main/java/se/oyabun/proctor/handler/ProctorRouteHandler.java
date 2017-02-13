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
 * ProctorRouteHandler interface.
 * All proctor handler must implement this.
 */
public interface ProctorRouteHandler {

    /**
     * Return URL for given handle name
     *
     * @param input      of request
     * @param properties for handler
     * @return complete url including requested uri for given handle name
     * @throws NoHandleForNameException if handle is not present
     * @throws MalformedURLException    on malformed URLs
     */
    URL resolveURLFor(final String input,
                      final ProctorHandlerConfiguration properties)
            throws
            NoHandleForNameException,
            MalformedURLException;

}

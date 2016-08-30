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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.oyabun.proctor.exceptions.InputNotMatchedException;
import se.oyabun.proctor.exceptions.NoHandleForNameException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Default abstract handler implementation
 */
public abstract class AbstractDefaultProctorRouteHandler
        implements ProctorRouteHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Pattern matcherPattern;

    private final String matcherHandle;

    public AbstractDefaultProctorRouteHandler(final String regex,
                                              final String matcherHandle) {

        log.info("Configuring default Proctor ProctorRouteHandler for '{}' with pattern '{}'.", matcherHandle, regex);

        this.matcherHandle = matcherHandle;

        this.matcherPattern = Pattern.compile(regex);

    }

    /**
     * Default regex pattern matching for input
     * @param uri to verify
     * @return true if constructor pattern matches input
     */
    public boolean matches(final String uri) {

        return matcherPattern.matcher(uri).matches();

    }


    /**
     * Default handle aquisition for given input (should be matched first)
     * @param uri to get handle for
     * @return handle name for given input
     * @throws InputNotMatchedException if input does not match
     */
    public String getHandleNameFor(final String uri)
            throws InputNotMatchedException {

        if(matcherPattern.matcher(uri).matches()) {

            return matcherHandle;

        } else {

            throw new InputNotMatchedException("Pattern does not match given input.");

        }

    }

    /**
     * Force implementation of URL resolver.
     * @param handleName to resolve complete URL for
     * @param uri of request
     * @return complete URL including requested uri for handle
     */
    public abstract URL resolveURLFor(final String handleName,
                                      final String uri)
            throws NoHandleForNameException, MalformedURLException;

    /**
     * Get matcher handle
     * @return matcher handle name
     */
    public Set<String> getHandleNames() {

        return new HashSet<>(Arrays.asList(matcherHandle));

    }

    /**
     * Return root URL for given handle name
     * @param handleName to get root url for
     * @return URL base for handle name
     */
    protected abstract URL getRootURLForHandleName(final String handleName);


}

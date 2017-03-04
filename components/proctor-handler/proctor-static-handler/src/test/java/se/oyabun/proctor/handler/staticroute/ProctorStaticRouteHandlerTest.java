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

import org.junit.Before;

import java.net.URL;

public class ProctorStaticRouteHandlerTest {

    private static final String MATCHER_PATTERN_REGEX = "/somepath/.*";
    private static final String MATCHER_HANDLE = "Some static route";
    private static final Boolean APPEND_ORIGINAL_PATH = true;

    private URL staticUrl;

    private ProctorStaticRouteHandler routeHandler;

    @Before
    public void before()
            throws
            Exception {

        staticUrl = new URL("https://somehost:8443");

        routeHandler = new ProctorStaticRouteHandler();

    }

}

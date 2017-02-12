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
package se.oyabun.proctor.handler.zookeeper;

import org.apache.curator.x.discovery.ServiceDiscovery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.oyabun.proctor.exceptions.URIParseException;

@RunWith(MockitoJUnitRunner.class)
public class ProctorZookeeperRouteHandlerTest {

    private ProctorZookeeperRouteHandler routeHandler;

    @Mock
    private ServiceDiscovery<Void> serviceDiscovery;

    @Before
    public void before() {

        routeHandler = new ProctorZookeeperRouteHandler();

    }


    @Test(expected = URIParseException.class)
    public void testParseURIWithNullURI() {

        ProctorZookeeperRouteHandler.parseURI(null);

    }


    @Test(expected = URIParseException.class)
    public void testParseURIWithNonMatchingURI() {

        ProctorZookeeperRouteHandler.parseURI("/URIThatDoesNotMatch");

    }

    @Test(expected = URIParseException.class)
    public void testParseURIWithNonMatchingURI2() {

        ProctorZookeeperRouteHandler.parseURI("//");

    }

    @Test(expected = URIParseException.class)
    public void testParseURIWithNonMatchingURI3() {

        ProctorZookeeperRouteHandler.parseURI("//uri");

    }

}

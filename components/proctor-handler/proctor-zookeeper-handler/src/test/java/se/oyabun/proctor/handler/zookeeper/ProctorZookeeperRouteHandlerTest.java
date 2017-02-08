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

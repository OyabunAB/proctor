package se.oyabun.proctor.handler.zookeeper;

import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.ServiceProviderBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.oyabun.proctor.exceptions.InputNotMatchedException;
import se.oyabun.proctor.exceptions.URIParseException;

import java.util.Set;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProctorZookeeperRouteProctorRouteHandlerTest {

    private ProctorZookeeperRouteProctorRouteHandler routeHandler;

    @Mock
    private ServiceDiscovery<Void> serviceDiscovery;

    @Before
    public void before() {

        routeHandler = new ProctorZookeeperRouteProctorRouteHandler();
        routeHandler.setServiceDiscovery(serviceDiscovery);

    }

    @Test
    public void testMatchesWithInvalidURI() {

        Assert.assertFalse(routeHandler.matches("invalid URI"));

    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMatchesWhenServiceProviderStartFails() throws Exception {

        ServiceProvider<Void> serviceProvider = mock(ServiceProvider.class);
        doThrow(new Exception()).when(serviceProvider).start();

        ServiceProviderBuilder<Void> serviceProviderBuilder = mock(ServiceProviderBuilder.class);
        when(serviceProviderBuilder.serviceName("/an")).thenReturn(serviceProviderBuilder);
        when(serviceProviderBuilder.build()).thenReturn(serviceProvider);

        when(serviceDiscovery.serviceProviderBuilder()).thenReturn(serviceProviderBuilder);

        Assert.assertFalse(routeHandler.matches("/an/uri"));

    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMatchesSuccessful() throws Exception {

        ServiceProvider<Void> serviceProvider = mock(ServiceProvider.class);

        ServiceProviderBuilder<Void> serviceProviderBuilder = mock(ServiceProviderBuilder.class);
        when(serviceProviderBuilder.serviceName("/an")).thenReturn(serviceProviderBuilder);
        when(serviceProviderBuilder.build()).thenReturn(serviceProvider);

        when(serviceDiscovery.serviceProviderBuilder()).thenReturn(serviceProviderBuilder);

        Assert.assertTrue(routeHandler.matches("/an/uri"));

    }

    @Test(expected = URIParseException.class)
    public void testParseURIWithNullURI() {

        ProctorZookeeperRouteProctorRouteHandler.parseURI(null);

    }


    @Test(expected = URIParseException.class)
    public void testParseURIWithNonMatchingURI() {

        ProctorZookeeperRouteProctorRouteHandler.parseURI("/URIThatDoesNotMatch");

    }

    @Test(expected = URIParseException.class)
    public void testParseURIWithNonMatchingURI2() {

        ProctorZookeeperRouteProctorRouteHandler.parseURI("//");

    }

    @Test(expected = URIParseException.class)
    public void testParseURIWithNonMatchingURI3() {

        ProctorZookeeperRouteProctorRouteHandler.parseURI("//uri");

    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetHandleNames() throws InputNotMatchedException {

        ServiceProvider<Void> serviceProvider = mock(ServiceProvider.class);

        ServiceProviderBuilder<Void> serviceProviderBuilder = mock(ServiceProviderBuilder.class);
        when(serviceProviderBuilder.serviceName("/car")).thenReturn(serviceProviderBuilder);
        when(serviceProviderBuilder.serviceName("/bus")).thenReturn(serviceProviderBuilder);
        when(serviceProviderBuilder.build()).thenReturn(serviceProvider);

        when(serviceDiscovery.serviceProviderBuilder()).thenReturn(serviceProviderBuilder);

        routeHandler.getHandleNameFor("/car/123");
        routeHandler.getHandleNameFor("/bus/321");

        Set<String> handleNames = routeHandler.getHandleNames();

        Assert.assertEquals(2, handleNames.size());
        Assert.assertTrue(handleNames.contains("/car"));
        Assert.assertTrue(handleNames.contains("/bus"));

    }

}

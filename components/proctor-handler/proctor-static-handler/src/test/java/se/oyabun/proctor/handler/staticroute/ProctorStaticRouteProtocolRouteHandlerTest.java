package se.oyabun.proctor.handler.staticroute;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import se.oyabun.proctor.exceptions.NoHandleForNameException;

import java.net.MalformedURLException;
import java.net.URL;

public class ProctorStaticRouteProtocolRouteHandlerTest {

    private static final String MATCHER_PATTERN_REGEX = "/somepath/.*";
    private static final String MATCHER_HANDLE = "Some static route";
    private static final Boolean APPEND_ORIGINAL_PATH = true;

    private URL staticUrl;

    private ProctorStaticRouteProctorRouteHandler routeHandler;

    @Before
    public void before() throws Exception {

        staticUrl = new URL("https://somehost:8443");

        routeHandler = new ProctorStaticRouteProctorRouteHandler(
                MATCHER_PATTERN_REGEX,
                MATCHER_HANDLE,
                staticUrl,
                APPEND_ORIGINAL_PATH);

    }

    @Test
    public void testGetRootURLForHandleName() {

        Assert.assertEquals(staticUrl, routeHandler.getRootURLForHandleName("some handle name"));

        Assert.assertEquals(staticUrl, routeHandler.getRootURLForHandleName("some other handle name"));

        Assert.assertEquals(staticUrl, routeHandler.getRootURLForHandleName("third handle name"));

    }

    @Test(expected = NoHandleForNameException.class)
    public void testResolveURLForWithInvalidHandleName() throws NoHandleForNameException, MalformedURLException {

        routeHandler.resolveURLFor("Incorrect route", "some uri");

    }

    @Test
    public void testResolveURLForWithGoodHandleName() throws NoHandleForNameException, MalformedURLException {

        Assert.assertEquals(new URL(staticUrl, "/some/uri"), routeHandler.resolveURLFor(MATCHER_HANDLE, "/some/uri"));

    }

    @Test
    public void testResolveURLForWhenAppendOriginalPathIsFalse()
            throws NoHandleForNameException, MalformedURLException {

        routeHandler =
                new ProctorStaticRouteProctorRouteHandler(MATCHER_PATTERN_REGEX, MATCHER_HANDLE, staticUrl, false);

        Assert.assertEquals(staticUrl, routeHandler.resolveURLFor(MATCHER_HANDLE, "/some/uri"));

    }
}

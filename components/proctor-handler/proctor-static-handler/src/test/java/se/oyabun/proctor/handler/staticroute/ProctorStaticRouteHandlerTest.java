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
    public void before() throws Exception {

        staticUrl = new URL("https://somehost:8443");

        routeHandler = new ProctorStaticRouteHandler();

    }

}

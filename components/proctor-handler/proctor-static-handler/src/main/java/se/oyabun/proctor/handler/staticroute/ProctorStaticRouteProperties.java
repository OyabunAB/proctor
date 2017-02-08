package se.oyabun.proctor.handler.staticroute;

import se.oyabun.proctor.handler.properties.ProctorHandlerProperties;

import java.util.HashMap;
import java.util.Map;

public class ProctorStaticRouteProperties
        implements ProctorHandlerProperties {

    private String configurationID;
    private int priority;
    private String pattern;
    private Map<String, String> properties = new HashMap<>();

    public ProctorStaticRouteProperties(final String configurationID,
                                         final int priority,
                                         final String pattern,
                                         final String appendPath,
                                         final String defaultUrl) {
        this.configurationID = configurationID;
        this.priority = priority;
        this.pattern = pattern;
        this.properties.put(ProctorStaticRouteHandler.APPEND_PATH_PROPERTY, appendPath);
        this.properties.put(ProctorStaticRouteHandler.DEFAULT_URL_PROPERTY, defaultUrl);

    }

    @Override
    public String getConfigurationID() {
        return configurationID;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public String getHandlerType() {
        return ProctorStaticRouteHandler.class.getSimpleName();
    }

    @Override
    public String getPattern() {
        return pattern;
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

}

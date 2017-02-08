package se.oyabun.proctor.handler.properties;

import java.io.Serializable;
import java.util.Map;

/**
 * Handler properties representation,
 * implementations of which is used to identify and configure a handler instance.
 */
public interface ProctorHandlerProperties
        extends Serializable {

    /**
     * Cluster/global unique handler identification string
     * @return identification string for handler instance
     */
    String getConfigurationID();

    /**
     * Priority value for handler properties, lower to higher will be selected
     * @return handler instance priority, used for ordering of handler
     */
    int getPriority();

    /**
     * Handler type information, used to find specific handler type, e.g.
     * specific handler interface/class to instantiate
     * @return name of handler type
     */
    String getHandlerType();

    /**
     * Handler matcher pattern
     * @return pattern for matching requests
     */
    String getPattern();

    /**
     * Handler interpreted property entries for configuration
     */
    Map<String, String> getProperties();

}

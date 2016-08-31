package se.oyabun.proctor.events.handler;

import org.springframework.context.ApplicationEvent;

/**
 * Proctor Handler Match Event
 */
public class ProxyHandlerMatchedEvent
        extends ApplicationEvent {

    /**
     * Used to represent a handler match
     * @param requestedUri requested resource uri
     */
    public ProxyHandlerMatchedEvent(String requestedUri) {

        super(requestedUri);

    }

}

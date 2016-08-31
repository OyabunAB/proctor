package se.oyabun.proctor.events.handler;

import org.springframework.context.ApplicationEvent;

/**
 * Proctor proxy handler non matched event
 */
public class ProxyHandlerNotMatchedEvent
        extends ApplicationEvent {

    /**
     * Used to represent a handler matcher miss
     * @param requestedUri requested resource uri
     */
    public ProxyHandlerNotMatchedEvent(String requestedUri) {

        super(requestedUri);

    }

}


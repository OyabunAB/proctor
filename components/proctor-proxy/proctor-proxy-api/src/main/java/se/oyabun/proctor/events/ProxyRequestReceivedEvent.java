package se.oyabun.proctor.events;

import org.springframework.context.ApplicationEvent;
import se.oyabun.proctor.http.HttpRequestData;

/**
 * Proctor proxy received event
 */
public class ProxyRequestReceivedEvent
        extends ApplicationEvent {

    public ProxyRequestReceivedEvent(HttpRequestData source) {

        super(source);

    }

}

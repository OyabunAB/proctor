package se.oyabun.proctor.events.http;

import org.springframework.context.ApplicationEvent;
import se.oyabun.proctor.http.HttpRequestData;

/**
 * Proctor Proxy Request Event
 */
public class ProxyRequestReceivedEvent
        extends ApplicationEvent {

    /**
     * Represents a request for a proxied resource
     * @param httpRequestData for the request
     */
    public ProxyRequestReceivedEvent(HttpRequestData httpRequestData) {

        super(httpRequestData);

    }

}

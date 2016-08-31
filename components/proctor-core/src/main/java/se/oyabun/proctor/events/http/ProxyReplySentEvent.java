package se.oyabun.proctor.events.http;

import org.springframework.context.ApplicationEvent;
import se.oyabun.proctor.http.HttpResponseData;

/**
 * Proctor Proxy Reply Event
 */
public class ProxyReplySentEvent
        extends ApplicationEvent {

    /**
     * Represents a reply from a proxied call
     * @param httpResponseData that was returned for a request
     */
    public ProxyReplySentEvent(HttpResponseData httpResponseData) {

        super(httpResponseData);

    }

}

package se.oyabun.proctor.events;

import org.springframework.context.ApplicationEvent;
import se.oyabun.proctor.http.HttpResponseData;

/**
 * Proctor proxy reply event
 */
public class ProxyReplySentEvent
        extends ApplicationEvent {

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public ProxyReplySentEvent(HttpResponseData source) {

        super(source);

    }

}

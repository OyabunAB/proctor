/*
 * Copyright 2016 Oyabun AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.oyabun.proctor.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import se.oyabun.proctor.events.http.ProxyReplySentEvent;
import se.oyabun.proctor.events.http.ProxyRequestReceivedEvent;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Abstract Proctor Proxy class, enabling lifecycle handling
 */
public abstract class AbstractProctorProxy
        implements ProctorProxy {

    private static final Logger logger = LoggerFactory.getLogger(AbstractProctorProxy.class);

    @EventListener
    final void trackRequestEvent(ProxyRequestReceivedEvent proxyRequestReceivedEvent) {

        if(logger.isDebugEnabled()) {

            logger.debug("Proxy received '{}'.", proxyRequestReceivedEvent);

        }

    }

    @EventListener
    final void trackReplyEvent(ProxyReplySentEvent proxyReplySentEvent) {

        if(logger.isDebugEnabled()) {

            logger.debug("Proxy returned '{}'.", proxyReplySentEvent);

        }

    }

    @PostConstruct
    public abstract void startProxy() throws Exception;

    @PreDestroy
    public abstract void stopProxy();

}

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
package se.oyabun.proctor.statistics.metrics.meters.http;

import com.codahale.metrics.Meter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import se.oyabun.proctor.events.http.ProxyReplySentEvent;
import se.oyabun.proctor.statistics.ProctorStatisticType;
import se.oyabun.proctor.statistics.ProctorStatisticsGatherer;
import se.oyabun.proctor.statistics.metrics.ProctorMetricsRegistry;
import se.oyabun.proctor.statistics.metrics.meters.AbstractProctorMeter;

/**
 * Proctor Proxy Response Meter
 */
@Component
public class ProctorProxyResponseMeter
        extends AbstractProctorMeter
        implements ProctorStatisticsGatherer {

    private Meter replies;

    @Autowired
    public ProctorProxyResponseMeter(final ProctorMetricsRegistry proctorMetricsRegistry) {

        this.replies = proctorMetricsRegistry.getMetricsRegistry()
                                             .meter(ProctorStatisticType.PROXY_REPLY_SENT.name());

    }

    /**
     * Listener method, feeds the meter
     *
     * @param proxyReplySentEvent published by the system
     */
    @EventListener
    public void incomingRequest(final ProxyReplySentEvent proxyReplySentEvent) {

        replies.mark();

    }

    /**
     * ${@inheritDoc}
     */
    public boolean gathers(ProctorStatisticType proctorStatisticType) {

        return ProctorStatisticType.PROXY_REPLY_SENT.equals(proctorStatisticType);

    }

    /**
     * ${@inheritDoc}
     */
    protected Meter getMeter() {

        return replies;

    }

}

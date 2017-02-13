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
package se.oyabun.proctor.statistics.metrics.meters.handler;

import com.codahale.metrics.Meter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import se.oyabun.proctor.events.handler.ProxyHandlerNotMatchedEvent;
import se.oyabun.proctor.statistics.ProctorStatisticType;
import se.oyabun.proctor.statistics.ProctorStatisticsGatherer;
import se.oyabun.proctor.statistics.metrics.ProctorMetricsRegistry;
import se.oyabun.proctor.statistics.metrics.meters.AbstractProctorMeter;

/**
 * Proctor Proxy Handler Misses Meter
 */
public class ProctorHandlerMissMeter
        extends AbstractProctorMeter
        implements ProctorStatisticsGatherer {

    private final Meter misses;

    @Autowired
    public ProctorHandlerMissMeter(final ProctorMetricsRegistry proctorMetricsRegistry) {

        misses = proctorMetricsRegistry.getMetricsRegistry()
                                       .meter(ProctorStatisticType.PROXY_HANDLER_MISS.name());

    }

    /**
     * Listener method, feeds the meter
     *
     * @param proxyHandlerNotMatchedEvent published by the system
     */
    @EventListener
    public void incomingRequest(final ProxyHandlerNotMatchedEvent proxyHandlerNotMatchedEvent) {

        misses.mark();

    }

    /**
     * ${@inheritDoc}
     */
    public boolean gathers(final ProctorStatisticType proctorStatisticType) {

        return ProctorStatisticType.PROXY_HANDLER_MISS.equals(proctorStatisticType);

    }

    /**
     * ${@inheritDoc}
     */
    protected Meter getMeter() {

        return misses;

    }

}

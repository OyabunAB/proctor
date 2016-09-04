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
import org.springframework.stereotype.Component;
import se.oyabun.proctor.events.handler.ProxyHandlerMatchedEvent;
import se.oyabun.proctor.statistics.ProctorStatistic;
import se.oyabun.proctor.statistics.ProctorStatisticsGatherer;
import se.oyabun.proctor.statistics.metrics.ProctorMetricsRegistry;
import se.oyabun.proctor.statistics.metrics.meters.AbstractProctorMeter;

import javax.annotation.PostConstruct;

/**
 * Proctor Proxy Handler Match Meter
 */
@Component
public class ProctorHandlerMatchMeter
        extends AbstractProctorMeter
        implements ProctorStatisticsGatherer {

    @Autowired
    private ProctorMetricsRegistry proctorMetricsRegistry;

    private Meter hits;

    @PostConstruct
    public void initMetricMeter() {

        hits = proctorMetricsRegistry.getMetricsRegistry()
                .meter(ProctorStatistic.PROXY_HANDLER_MATCH.name());

    }

    @EventListener
    public void incomingRequest(ProxyHandlerMatchedEvent proxyHandlerMatchedEvent) {

        hits.mark();

    }

    /**
     * ${@inheritDoc}
     */
    public boolean gathers(final ProctorStatistic proctorStatistic) {

        return ProctorStatistic.PROXY_HANDLER_MATCH.equals(proctorStatistic);

    }

    /**
     * ${@inheritDoc}
     */
    protected Meter getMeter() {

        return hits;

    }

}

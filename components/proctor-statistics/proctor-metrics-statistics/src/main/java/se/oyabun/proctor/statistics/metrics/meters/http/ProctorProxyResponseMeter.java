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
import se.oyabun.proctor.statistics.ProctorStatistic;
import se.oyabun.proctor.statistics.ProctorStatisticsGatherer;
import se.oyabun.proctor.statistics.metrics.ProctorMetricsRegistry;
import se.oyabun.proctor.statistics.metrics.meters.AbstractProctorMeter;

import javax.annotation.PostConstruct;

/**
 * Proctor Proxy Response Meter
 */
@Component
public class ProctorProxyResponseMeter
        extends AbstractProctorMeter
        implements ProctorStatisticsGatherer {

    @Autowired
    private ProctorMetricsRegistry proctorMetricsRegistry;

    private Meter replies;

    @PostConstruct
    public void initMetricMeter() {

        replies = proctorMetricsRegistry.getMetricsRegistry()
                .meter(ProctorStatistic.PROXY_REPLY_SENT.name());

    }

    @EventListener
    public void incomingRequest(ProxyReplySentEvent proxyReplySentEvent) {

        replies.mark();

    }

    @Override
    public boolean gathers(ProctorStatistic proctorStatistic) {

        return ProctorStatistic.PROXY_REPLY_SENT.equals(proctorStatistic);

    }

    /**
     * ${@inheritDoc}
     */
    protected Meter getMeter() {

        return replies;

    }

}

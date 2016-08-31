package se.oyabun.proctor.statistics.metrics.meters.handler;

import com.codahale.metrics.Meter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import se.oyabun.proctor.events.handler.ProxyHandlerNotMatchedEvent;
import se.oyabun.proctor.statistics.ProctorStatistic;
import se.oyabun.proctor.statistics.metrics.ProctorMetricsRegistry;

import javax.annotation.PostConstruct;

/**
 * Proctor Proxy Handler Misses Meter
 */
public class ProctorHandlerMissMeter {

    @Autowired
    private ProctorMetricsRegistry proctorMetricsRegistry;

    private Meter misses;

    @PostConstruct
    public void initMetricMeter() {

        misses = proctorMetricsRegistry.getMetricsRegistry()
                .meter(ProctorStatistic.PROXY_HANDLER_MISS.name());

    }

    @EventListener
    public void incomingRequest(ProxyHandlerNotMatchedEvent proxyHandlerNotMatchedEvent) {

        misses.mark();

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

        return misses;

    }

}

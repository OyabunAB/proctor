package se.oyabun.proctor.statistics.metrics.meters.http;

import com.codahale.metrics.Meter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import se.oyabun.proctor.events.http.ProxyRequestReceivedEvent;
import se.oyabun.proctor.statistics.ProctorStatistic;
import se.oyabun.proctor.statistics.ProctorStatisticsGatherer;
import se.oyabun.proctor.statistics.metrics.ProctorMetricsRegistry;
import se.oyabun.proctor.statistics.metrics.meters.AbstractProctorMeter;

import javax.annotation.PostConstruct;

/**
 * Proctor Proxy Request Meter
 */
@Component
public class ProctorProxyRequestMeter
        extends AbstractProctorMeter
        implements ProctorStatisticsGatherer {

    @Autowired
    private ProctorMetricsRegistry proctorMetricsRegistry;

    private Meter requests;

    @PostConstruct
    public void initMetricMeter() {

         requests = proctorMetricsRegistry.getMetricsRegistry()
                         .meter(ProctorStatistic.PROXY_REQUEST_RECEIVED.name());

    }

    @EventListener
    public void incomingRequest(ProxyRequestReceivedEvent proxyRequestReceivedEvent) {

        requests.mark();

    }

    /**
     * ${@inheritDoc}
     */
    public boolean gathers(final ProctorStatistic proctorStatistic) {

        return ProctorStatistic.PROXY_REQUEST_RECEIVED.equals(proctorStatistic);

    }

    /**
     * ${@inheritDoc}
     */
    protected Meter getMeter() {

        return requests;

    }

}

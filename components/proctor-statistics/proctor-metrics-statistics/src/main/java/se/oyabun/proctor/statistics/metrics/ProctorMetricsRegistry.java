package se.oyabun.proctor.statistics.metrics;

import com.codahale.metrics.MetricRegistry;
import org.springframework.stereotype.Component;

/**
 * Proctor Metrics Registry Bean
 */
@Component
public class ProctorMetricsRegistry {

    final MetricRegistry metricsRegistry = new MetricRegistry();

    public MetricRegistry getMetricsRegistry() {

        return metricsRegistry;

    }

}

package se.oyabun.proctor.statistics.metrics.meters.http;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.oyabun.proctor.events.http.ProxyReplySentEvent;
import se.oyabun.proctor.exceptions.NonGatheredStatisticRequestException;
import se.oyabun.proctor.http.HttpResponseData;
import se.oyabun.proctor.statistics.ProctorStatistic;
import se.oyabun.proctor.statistics.metrics.ProctorMetricsRegistry;

import java.math.BigInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Proctor Proxy Response Meter test
 */
@RunWith(MockitoJUnitRunner.class)
public class ProctorProxyResponseMeterTest {

    private static final String PROXY_URL = "/proxied";

    private ProctorProxyResponseMeter proctorProxyResponseMeter;

    @Mock
    private ProctorMetricsRegistry mockedProctorMetricsRegistry;

    @Mock
    private MetricRegistry mockedMetricsRegistry;

    @Mock
    private Meter mockedMeter;

    @Mock
    private HttpResponseData mockedHttpResponseData;

    @Before
    public void setup() {

        when(mockedProctorMetricsRegistry.getMetricsRegistry()).thenReturn(mockedMetricsRegistry);
        when(mockedMetricsRegistry.meter(anyString())).thenReturn(mockedMeter);
        proctorProxyResponseMeter = new ProctorProxyResponseMeter(mockedProctorMetricsRegistry);

    }

    @Test
    public void verifyStatisticsGatherer() {

        assertTrue(proctorProxyResponseMeter.gathers(ProctorStatistic.PROXY_REPLY_SENT));

    }

    @Test
    public void makedWhenEventIsTriggered() {

        proctorProxyResponseMeter.incomingRequest(new ProxyReplySentEvent(mockedHttpResponseData));

        verify(mockedMeter, times(1)).mark();

    }

    @Test
    public void returningCountFor() throws NonGatheredStatisticRequestException {

        when(mockedMeter.getCount()).thenReturn(BigInteger.ONE.longValue());

        assertThat(proctorProxyResponseMeter.getCountFor(ProctorStatistic.PROXY_REPLY_SENT), is(BigInteger.ONE));

    }

    @Test(expected = NonGatheredStatisticRequestException.class)
    public void notReturningOnOtherStatistic() throws NonGatheredStatisticRequestException {

        proctorProxyResponseMeter.getCountFor(ProctorStatistic.PROXY_REQUEST_RECEIVED);

    }

}

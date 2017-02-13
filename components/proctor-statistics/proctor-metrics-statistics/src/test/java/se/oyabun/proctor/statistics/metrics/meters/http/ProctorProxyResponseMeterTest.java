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
import com.codahale.metrics.MetricRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.oyabun.proctor.events.http.ProxyReplySentEvent;
import se.oyabun.proctor.exceptions.NonGatheredStatisticRequestException;
import se.oyabun.proctor.http.HttpResponseData;
import se.oyabun.proctor.statistics.ProctorStatisticType;
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

        assertTrue(proctorProxyResponseMeter.gathers(ProctorStatisticType.PROXY_REPLY_SENT));

    }

    @Test
    public void makedWhenEventIsTriggered() {

        proctorProxyResponseMeter.incomingRequest(new ProxyReplySentEvent(mockedHttpResponseData));

        verify(mockedMeter,
               times(1)).mark();

    }

    @Test
    public void returningCountFor()
            throws
            NonGatheredStatisticRequestException {

        when(mockedMeter.getCount()).thenReturn(BigInteger.ONE.longValue());

        assertThat(proctorProxyResponseMeter.getCountFor(ProctorStatisticType.PROXY_REPLY_SENT),
                   is(BigInteger.ONE));

    }

    @Test(expected = NonGatheredStatisticRequestException.class)
    public void notReturningOnOtherStatistic()
            throws
            NonGatheredStatisticRequestException {

        proctorProxyResponseMeter.getCountFor(ProctorStatisticType.PROXY_REQUEST_RECEIVED);

    }

}

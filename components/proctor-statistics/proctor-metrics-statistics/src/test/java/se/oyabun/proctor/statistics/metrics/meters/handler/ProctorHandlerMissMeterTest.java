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
import com.codahale.metrics.MetricRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.oyabun.proctor.events.handler.ProxyHandlerNotMatchedEvent;
import se.oyabun.proctor.exceptions.NonGatheredStatisticRequestException;
import se.oyabun.proctor.statistics.ProctorStatisticType;
import se.oyabun.proctor.statistics.metrics.ProctorMetricsRegistry;

import java.math.BigInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Proctor Handler Miss Matcher test
 */
@RunWith(MockitoJUnitRunner.class)
public class ProctorHandlerMissMeterTest {

    private static final String MISSED_URL = "/missed";

    private ProctorHandlerMissMeter proctorHandlerMissMeter;

    @Mock
    private ProctorMetricsRegistry mockedProctorMetricsRegistry;

    @Mock
    private MetricRegistry mockedMetricsRegistry;

    @Mock
    private Meter mockedMeter;

    @Before
    public void setup() {

        when(mockedProctorMetricsRegistry.getMetricsRegistry()).thenReturn(mockedMetricsRegistry);
        when(mockedMetricsRegistry.meter(anyString())).thenReturn(mockedMeter);
        proctorHandlerMissMeter = new ProctorHandlerMissMeter(mockedProctorMetricsRegistry);

    }

    @Test
    public void verifyStatisticsGatherer() {

        assertTrue(proctorHandlerMissMeter.gathers(ProctorStatisticType.PROXY_HANDLER_MISS));

    }

    @Test
    public void makedWhenEventIsTriggered() {

        proctorHandlerMissMeter.incomingRequest(new ProxyHandlerNotMatchedEvent(MISSED_URL));

        verify(mockedMeter,
               times(1)).mark();

    }

    @Test
    public void returningCountFor()
            throws
            NonGatheredStatisticRequestException {

        when(mockedMeter.getCount()).thenReturn(BigInteger.ONE.longValue());

        assertThat(proctorHandlerMissMeter.getCountFor(ProctorStatisticType.PROXY_HANDLER_MISS),
                   is(BigInteger.ONE));

    }

    @Test(expected = NonGatheredStatisticRequestException.class)
    public void notReturningOnOtherStatistic()
            throws
            NonGatheredStatisticRequestException {

        proctorHandlerMissMeter.getCountFor(ProctorStatisticType.PROXY_HANDLER_MATCH);

    }

}

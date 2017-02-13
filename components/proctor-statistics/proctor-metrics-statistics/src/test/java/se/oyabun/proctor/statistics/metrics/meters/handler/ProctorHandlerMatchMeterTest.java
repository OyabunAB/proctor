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
import se.oyabun.proctor.events.handler.ProxyHandlerMatchedEvent;
import se.oyabun.proctor.exceptions.NonGatheredStatisticRequestException;
import se.oyabun.proctor.statistics.ProctorStatisticType;
import se.oyabun.proctor.statistics.metrics.ProctorMetricsRegistry;

import java.math.BigInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Proctor Handler Match Meter Tests
 */
@RunWith(MockitoJUnitRunner.class)
public class ProctorHandlerMatchMeterTest {

    private static final String MATCHED_URL = "/matchingurl";

    private ProctorHandlerMatchMeter proctorHandlerMatchMeter;

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
        proctorHandlerMatchMeter = new ProctorHandlerMatchMeter(mockedProctorMetricsRegistry);

    }

    @Test
    public void verifyStatisticsGatherer() {

        assertTrue(proctorHandlerMatchMeter.gathers(ProctorStatisticType.PROXY_HANDLER_MATCH));

    }

    @Test
    public void makedWhenEventIsTriggered() {

        proctorHandlerMatchMeter.incomingRequest(new ProxyHandlerMatchedEvent(MATCHED_URL));

        verify(mockedMeter,
               times(1)).mark();

    }

    @Test
    public void returningCountFor()
            throws
            NonGatheredStatisticRequestException {

        when(mockedMeter.getCount()).thenReturn(BigInteger.ONE.longValue());

        assertThat(proctorHandlerMatchMeter.getCountFor(ProctorStatisticType.PROXY_HANDLER_MATCH),
                   is(BigInteger.ONE));

    }

    @Test(expected = NonGatheredStatisticRequestException.class)
    public void notReturningOnOtherStatistic()
            throws
            NonGatheredStatisticRequestException {

        proctorHandlerMatchMeter.getCountFor(ProctorStatisticType.PROXY_HANDLER_MISS);

    }

}

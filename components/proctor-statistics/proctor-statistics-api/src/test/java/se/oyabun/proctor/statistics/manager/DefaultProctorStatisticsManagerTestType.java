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
package se.oyabun.proctor.statistics.manager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.oyabun.proctor.exceptions.NonGatheredStatisticRequestException;
import se.oyabun.proctor.statistics.ProctorStatisticType;
import se.oyabun.proctor.statistics.ProctorStatisticsGatherer;
import se.oyabun.proctor.statistics.ProctorStatisticsReport;

import java.math.BigInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Test cases for Default Proctor Statistics manager
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultProctorStatisticsManagerTestType {

    private static final BigInteger COUNT_VALUE = BigInteger.TEN;

    private DefaultProctorStatisticsManager defaultProctorStatisticsManager;

    @Mock
    private ProctorStatisticsGatherer mockedMatchingProctorStatisticsGatherer;

    @Mock
    private ProctorStatisticsGatherer mockedNonMatchingProctorStatisticsGatherer;


    @Before
    public void initTests() {

        defaultProctorStatisticsManager = new DefaultProctorStatisticsManager(mockedMatchingProctorStatisticsGatherer,
                                                                              mockedNonMatchingProctorStatisticsGatherer);

        when(mockedMatchingProctorStatisticsGatherer.gathers(any(ProctorStatisticType.class))).thenReturn(true);

        when(mockedNonMatchingProctorStatisticsGatherer.gathers(any(ProctorStatisticType.class))).thenReturn(false);

    }

    @Test
    public void testDefaultProcotorStatisticManager()
            throws
            NonGatheredStatisticRequestException {

        when(mockedMatchingProctorStatisticsGatherer.getCountFor(ProctorStatisticType.PROXY_HANDLER_MATCH)).thenReturn
                (COUNT_VALUE);

        verify(mockedNonMatchingProctorStatisticsGatherer,
               never()).gathers(ProctorStatisticType.PROXY_HANDLER_MATCH);

        ProctorStatisticsReport[] proctorStatisticsReports = defaultProctorStatisticsManager.getStatisticsFor
                (ProctorStatisticType.PROXY_HANDLER_MATCH);

        assertThat(proctorStatisticsReports.length,
                   is(1));
        assertThat(proctorStatisticsReports[ 0 ].getCountValue(),
                   is(COUNT_VALUE));

    }

}

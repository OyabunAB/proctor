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
package se.oyabun.proctor.statistics;

import se.oyabun.proctor.exceptions.NonGatheredStatisticRequestException;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Proctor Statistics Gatherer
 */
public interface ProctorStatisticsGatherer {


    /**
     * True if proctor statistics gatherer gathers given statistic
     *
     * @param proctorStatisticType to verify
     * @return true if gatherer gathers statistics for given statistic
     */
    boolean gathers(final ProctorStatisticType proctorStatisticType);

    /**
     * Gets mean value for given proctor statistic
     *
     * @param proctorStatisticType to get mean value for
     * @return mean value
     * @throws NonGatheredStatisticRequestException
     */
    BigDecimal getMeanFor(final ProctorStatisticType proctorStatisticType)
            throws
            NonGatheredStatisticRequestException;

    /**
     * Gets total count for given proctor statistic
     *
     * @param proctorStatisticType to get count for
     * @return total count
     * @throws NonGatheredStatisticRequestException
     */
    BigInteger getCountFor(final ProctorStatisticType proctorStatisticType)
            throws
            NonGatheredStatisticRequestException;

    /**
     * Gets rate over fifteen minutes for given proctor statistic
     *
     * @param proctorStatisticType to get fifteen minute rate for
     * @return rate over fifteen mintues
     * @throws NonGatheredStatisticRequestException
     */
    BigDecimal getFifteenMinuteRateFor(final ProctorStatisticType proctorStatisticType)
            throws
            NonGatheredStatisticRequestException;

    /**
     * Gets rate over five minutes for given proctor statistic
     *
     * @param proctorStatisticType to get five minute rate for
     * @return rate over five minutes
     * @throws NonGatheredStatisticRequestException
     */
    BigDecimal getFiveMinuteRateFor(final ProctorStatisticType proctorStatisticType)
            throws
            NonGatheredStatisticRequestException;

    /**
     * Gets rate over one minute for given proctor statistic
     *
     * @param proctorStatisticType tp get one minute rate for
     * @return rate over one minute
     * @throws NonGatheredStatisticRequestException
     */
    BigDecimal getOneMinuteRateFor(final ProctorStatisticType proctorStatisticType)
            throws
            NonGatheredStatisticRequestException;

}

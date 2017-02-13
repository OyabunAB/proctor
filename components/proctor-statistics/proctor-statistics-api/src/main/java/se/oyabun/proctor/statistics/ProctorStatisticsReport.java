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

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Proctor Statistic Report
 */
public class ProctorStatisticsReport {

    private ProctorStatisticType proctorStatisticType;

    private BigDecimal meanValue;

    private BigInteger countValue;

    private BigDecimal fifteenMinuteRateValue;

    private BigDecimal fiveMinuteRateValue;

    private BigDecimal oneMinuteRateValue;

    public ProctorStatisticsReport(final ProctorStatisticType proctorStatisticType,
                                   final BigDecimal meanValue,
                                   final BigInteger countValue,
                                   final BigDecimal fifteenMinuteRateValue,
                                   final BigDecimal fiveMinuteRateValue,
                                   final BigDecimal oneMinuteRateValue) {

        this.proctorStatisticType = proctorStatisticType;
        this.meanValue = meanValue;
        this.countValue = countValue;
        this.fifteenMinuteRateValue = fifteenMinuteRateValue;
        this.fiveMinuteRateValue = fiveMinuteRateValue;
        this.oneMinuteRateValue = oneMinuteRateValue;

    }

    public ProctorStatisticType getProctorStatisticType() {

        return proctorStatisticType;

    }

    public BigDecimal getMeanValue() {

        return meanValue;

    }

    public BigInteger getCountValue() {

        return countValue;

    }

    public BigDecimal getFifteenMinuteRateValue() {

        return fifteenMinuteRateValue;

    }

    public BigDecimal getFiveMinuteRateValue() {

        return fiveMinuteRateValue;

    }

    public BigDecimal getOneMinuteRateValue() {

        return oneMinuteRateValue;

    }

}

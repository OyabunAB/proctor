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
package se.oyabun.proctor.statistics.metrics.meters;

import com.codahale.metrics.Meter;
import se.oyabun.proctor.exceptions.NonGatheredStatisticRequestException;
import se.oyabun.proctor.statistics.ProctorStatisticType;
import se.oyabun.proctor.statistics.ProctorStatisticsGatherer;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Abstract Meter Implementation
 */
public abstract class AbstractProctorMeter
        implements ProctorStatisticsGatherer {

    /**
     * ${@inheritDoc}
     */
    public BigDecimal getMeanFor(final ProctorStatisticType proctorStatisticType)
            throws
            NonGatheredStatisticRequestException {

        if (this.gathers(proctorStatisticType)) {

            return BigDecimal.valueOf(getMeter().getMeanRate());

        } else {

            throw new NonGatheredStatisticRequestException("For given proctor statistic '" + proctorStatisticType + "'");

        }

    }

    protected abstract Meter getMeter();

    /**
     * ${@inheritDoc}
     */
    public BigInteger getCountFor(final ProctorStatisticType proctorStatisticType)
            throws
            NonGatheredStatisticRequestException {

        if (this.gathers(proctorStatisticType)) {

            return BigInteger.valueOf(getMeter().getCount());

        } else {

            throw new NonGatheredStatisticRequestException("For given proctor statistic '" + proctorStatisticType + "'");

        }

    }

    /**
     * ${@inheritDoc}
     */
    public BigDecimal getFifteenMinuteRateFor(final ProctorStatisticType proctorStatisticType)
            throws
            NonGatheredStatisticRequestException {

        if (this.gathers(proctorStatisticType)) {

            return BigDecimal.valueOf(getMeter().getFifteenMinuteRate());

        } else {

            throw new NonGatheredStatisticRequestException("For given proctor statistic '" + proctorStatisticType + "'");

        }

    }

    /**
     * ${@inheritDoc}
     */
    public BigDecimal getFiveMinuteRateFor(final ProctorStatisticType proctorStatisticType)
            throws
            NonGatheredStatisticRequestException {

        if (this.gathers(proctorStatisticType)) {

            return BigDecimal.valueOf(getMeter().getFiveMinuteRate());

        } else {

            throw new NonGatheredStatisticRequestException("For given proctor statistic '" + proctorStatisticType + "'");

        }

    }

    /**
     * ${@inheritDoc}
     */
    public BigDecimal getOneMinuteRateFor(final ProctorStatisticType proctorStatisticType)
            throws
            NonGatheredStatisticRequestException {

        if (this.gathers(proctorStatisticType)) {

            return BigDecimal.valueOf(getMeter().getOneMinuteRate());

        } else {

            throw new NonGatheredStatisticRequestException("For given proctor statistic '" + proctorStatisticType + "'");

        }

    }

}

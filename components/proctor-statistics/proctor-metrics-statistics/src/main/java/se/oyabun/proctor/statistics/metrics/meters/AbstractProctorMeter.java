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
import se.oyabun.proctor.statistics.ProctorStatistic;
import se.oyabun.proctor.statistics.ProctorStatisticsGatherer;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Abstract Meter Implementation
 */
public abstract class AbstractProctorMeter
        implements ProctorStatisticsGatherer {

    protected abstract Meter getMeter();

    /**
     * ${@inheritDoc}
     */
    public BigDecimal getMeanFor(final ProctorStatistic proctorStatistic)
            throws NonGatheredStatisticRequestException {

        if(this.gathers(proctorStatistic)) {

            return BigDecimal.valueOf(getMeter().getMeanRate());

        } else {

            throw new NonGatheredStatisticRequestException("For given proctor statistic '"+proctorStatistic+"'");

        }

    }

    /**
     * ${@inheritDoc}
     */
    public BigInteger getCountFor(final ProctorStatistic proctorStatistic)
            throws NonGatheredStatisticRequestException {

        if(this.gathers(proctorStatistic)) {

            return BigInteger.valueOf(getMeter().getCount());

        } else {

            throw new NonGatheredStatisticRequestException("For given proctor statistic '"+proctorStatistic+"'");

        }

    }

    /**
     * ${@inheritDoc}
     */
    public BigDecimal getFifteenMinuteRateFor(final ProctorStatistic proctorStatistic)
            throws NonGatheredStatisticRequestException {

        if(this.gathers(proctorStatistic)) {

            return BigDecimal.valueOf(getMeter().getFifteenMinuteRate());

        } else {

            throw new NonGatheredStatisticRequestException("For given proctor statistic '"+proctorStatistic+"'");

        }

    }

    /**
     * ${@inheritDoc}
     */
    public BigDecimal getFiveMinuteRateFor(final ProctorStatistic proctorStatistic)
            throws NonGatheredStatisticRequestException {

        if(this.gathers(proctorStatistic)) {

            return BigDecimal.valueOf(getMeter().getFiveMinuteRate());

        } else {

            throw new NonGatheredStatisticRequestException("For given proctor statistic '"+proctorStatistic+"'");

        }

    }

    /**
     * ${@inheritDoc}
     */
    public BigDecimal getOneMinuteRateFor(final ProctorStatistic proctorStatistic)
            throws NonGatheredStatisticRequestException {

        if(this.gathers(proctorStatistic)) {

            return BigDecimal.valueOf(getMeter().getOneMinuteRate());

        } else {

            throw new NonGatheredStatisticRequestException("For given proctor statistic '"+proctorStatistic+"'");

        }

    }

}

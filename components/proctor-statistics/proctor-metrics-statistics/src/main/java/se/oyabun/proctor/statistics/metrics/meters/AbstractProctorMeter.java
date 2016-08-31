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

    @Override
    public BigDecimal getOneMinuteRateFor(final ProctorStatistic proctorStatistic)
            throws NonGatheredStatisticRequestException {

        if(this.gathers(proctorStatistic)) {

            return BigDecimal.valueOf(getMeter().getOneMinuteRate());

        } else {

            throw new NonGatheredStatisticRequestException("For given proctor statistic '"+proctorStatistic+"'");

        }

    }

}

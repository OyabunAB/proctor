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
     * @param proctorStatistic to verify
     * @return true if gatherer gathers statistics for given statistic
     */
    boolean gathers(final ProctorStatistic proctorStatistic);

    /**
     * Return mean value for
     * @param proctorStatistic
     * @return
     * @throws NonGatheredStatisticRequestException
     */
    BigDecimal getMeanFor(final ProctorStatistic proctorStatistic)
            throws NonGatheredStatisticRequestException;

    BigInteger getCountFor(final ProctorStatistic proctorStatistic)
            throws NonGatheredStatisticRequestException;

    BigDecimal getFifteenMinuteRateFor(final ProctorStatistic proctorStatistic)
            throws NonGatheredStatisticRequestException;

    BigDecimal getFiveMinuteRateFor(final ProctorStatistic proctorStatistic)
            throws NonGatheredStatisticRequestException;

    BigDecimal getOneMinuteRateFor(final ProctorStatistic proctorStatistic)
            throws NonGatheredStatisticRequestException;

}

package se.oyabun.proctor.statistics;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Proctor Statistic Report
 */
public class ProctorStatisticsReport {

    private ProctorStatistic proctorStatistic;

    private BigDecimal meanValue;

    private BigInteger countValueValue;

    private BigDecimal fifteenMinuteRateValue;

    private BigDecimal fiveMinuteRateValue;

    private BigDecimal oneMinuteRateValue;

    public ProctorStatisticsReport(final ProctorStatistic proctorStatistic,
                                   final BigDecimal meanValue,
                                   final BigInteger countValue,
                                   final BigDecimal fifteenMinuteRateValue,
                                   final BigDecimal fiveMinuteRateValue,
                                   final BigDecimal oneMinuteRateValue) {

        this.proctorStatistic = proctorStatistic;
        this.meanValue = meanValue;


    }

    public ProctorStatistic getProctorStatistic() {

        return proctorStatistic;

    }

    public BigDecimal getMeanValue() {

        return meanValue;

    }

    public BigInteger getCountValueValue() {

        return countValueValue;

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

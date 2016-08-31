package se.oyabun.proctor.statistics.manager;

import se.oyabun.proctor.statistics.ProctorStatistic;
import se.oyabun.proctor.statistics.ProctorStatisticsReport;

import java.util.Collection;

/**
 *
 */
public interface ProctorStatisticsManager {

    /**
     * Generate a proctor average statistics report on a statistic.
     * @param proctorStatistic to generate report for
     * @return report
     */
    ProctorStatisticsReport getStatisticsFor(ProctorStatistic proctorStatistic);

    /**
     * Return all proctor average statistics reports
     * @return list of all collected statistics as reports
     */
    Collection<ProctorStatisticsReport> getAllStatistics();

}

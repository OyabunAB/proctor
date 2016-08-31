package se.oyabun.proctor.statistics.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.oyabun.proctor.exceptions.NonGatheredStatisticRequestException;
import se.oyabun.proctor.statistics.ProctorStatistic;
import se.oyabun.proctor.statistics.ProctorStatisticsGatherer;
import se.oyabun.proctor.statistics.ProctorStatisticsReport;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Default Proctor Statistics Manager
 */
@Component
public class DefaultProctorStatisticsManager
        implements ProctorStatisticsManager{

    public static final Logger logger = LoggerFactory.getLogger(DefaultProctorStatisticsManager.class);

    @Autowired
    private List<ProctorStatisticsGatherer> proctorStatisticsGatherers;

    @Override
    public ProctorStatisticsReport getStatisticsFor(final ProctorStatistic proctorStatistic) {

        Optional<ProctorStatisticsGatherer> optionalProctorStatisticsGatherer =
                proctorStatisticsGatherers
                        .stream()
                        .filter(proctorStatisticsGatherer -> proctorStatisticsGatherer.gathers(proctorStatistic))
                        .findFirst();
        if(optionalProctorStatisticsGatherer.isPresent()) {

            ProctorStatisticsGatherer proctorStatisticsGatherer = optionalProctorStatisticsGatherer.get();

            try {

                return new ProctorStatisticsReport(
                        proctorStatistic,
                        proctorStatisticsGatherer.getMeanFor(proctorStatistic),
                        proctorStatisticsGatherer.getCountFor(proctorStatistic),
                        proctorStatisticsGatherer.getFifteenMinuteRateFor(proctorStatistic),
                        proctorStatisticsGatherer.getFiveMinuteRateFor(proctorStatistic),
                        proctorStatisticsGatherer.getOneMinuteRateFor(proctorStatistic));

            } catch (NonGatheredStatisticRequestException e) {

                if(logger.isErrorEnabled()) {

                    logger.error("Failed to fetch statistics for '"+proctorStatistic+"'.", e);

                }

            }

        }

        return null;

    }

    @Override
    public Collection<ProctorStatisticsReport> getAllStatistics() {

        final Map<ProctorStatistic, List<ProctorStatisticsReport>> proctorStatisticListMap = new HashMap<>();
        for(ProctorStatistic proctorStatistic : ProctorStatistic.values()) {

            final List<ProctorStatisticsGatherer> matchingRroctProctorStatisticsGatherers = new ArrayList<>();

            for(ProctorStatisticsGatherer proctorStatisticsGatherer : proctorStatisticsGatherers) {

                if(proctorStatisticsGatherer.gathers(proctorStatistic)) {

                    matchingRroctProctorStatisticsGatherers.add(proctorStatisticsGatherer);

                }

            }



                proctorStatisticListMap.put(
                        proctorStatistic,
                        matchingRroctProctorStatisticsGatherers
                                .stream()
                                .map(proctorStatisticsGatherer ->
                                        {
                                            try {

                                                return new ProctorStatisticsReport(
                                                        proctorStatistic,
                                                        proctorStatisticsGatherer.getMeanFor(proctorStatistic),
                                                        proctorStatisticsGatherer.getCountFor(proctorStatistic),
                                                        proctorStatisticsGatherer.getFifteenMinuteRateFor(proctorStatistic),
                                                        proctorStatisticsGatherer.getFiveMinuteRateFor(proctorStatistic),
                                                        proctorStatisticsGatherer.getOneMinuteRateFor(proctorStatistic));

                                            } catch (NonGatheredStatisticRequestException e) {

                                                if(logger.isErrorEnabled()) {

                                                    logger.error("Failed to fetch statistics for '"+proctorStatistic+"'.", e);

                                                }

                                                return null;

                                            }

                                        })
                                .collect(Collectors.toList()));



        }

        final List<ProctorStatisticsReport> allReports = new ArrayList<>();
        proctorStatisticListMap.values()
                .forEach(proctorStatisticsReports ->
                        allReports.addAll(proctorStatisticsReports));

        return allReports;

    }

}

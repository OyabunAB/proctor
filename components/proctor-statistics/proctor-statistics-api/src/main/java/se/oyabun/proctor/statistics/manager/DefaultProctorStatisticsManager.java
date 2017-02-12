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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.oyabun.proctor.exceptions.NonGatheredStatisticRequestException;
import se.oyabun.proctor.statistics.ProctorStatistic;
import se.oyabun.proctor.statistics.ProctorStatisticsGatherer;
import se.oyabun.proctor.statistics.ProctorStatisticsReport;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Default Proctor Statistics Manager
 */
@Component
public class DefaultProctorStatisticsManager
        implements ProctorStatisticsManager {

    public static final Logger logger = LoggerFactory.getLogger(DefaultProctorStatisticsManager.class);

    private final ProctorStatisticsGatherer[] proctorStatisticsGatherers;

    @Autowired
    public DefaultProctorStatisticsManager(ProctorStatisticsGatherer... proctorStatisticsGatherers) {

        this.proctorStatisticsGatherers = proctorStatisticsGatherers;

    }

    /**
     * ${@inheritDoc}
     */
    ProctorStatisticsGatherer[] getProctorStatisticsGatherersFor(final ProctorStatistic proctorStatistic) {

        return Arrays.stream(proctorStatisticsGatherers)
                     .filter(proctorStatisticsGatherer -> proctorStatisticsGatherer.gathers(proctorStatistic))
                     .toArray(ProctorStatisticsGatherer[]::new);

    }

    /**
     * ${@inheritDoc}
     */
    public ProctorStatisticsReport[] getStatisticsFor(final ProctorStatistic proctorStatistic) {

        return Arrays.stream(getProctorStatisticsGatherersFor(proctorStatistic))
                     .map(proctorStatisticsGatherer -> {
                         try {
                             return new ProctorStatisticsReport(proctorStatistic,
                                                                proctorStatisticsGatherer.getMeanFor(proctorStatistic),
                                                                proctorStatisticsGatherer.getCountFor(proctorStatistic),
                                                                proctorStatisticsGatherer.getFifteenMinuteRateFor
                                                                        (proctorStatistic),
                                                                proctorStatisticsGatherer.getFiveMinuteRateFor
                                                                        (proctorStatistic),
                                                                proctorStatisticsGatherer.getOneMinuteRateFor
                                                                        (proctorStatistic));
                         } catch (NonGatheredStatisticRequestException e) {

                             if (logger.isErrorEnabled()) {

                                 logger.error("Failed to fetch statistics for '" + proctorStatistic + "'.",
                                              e);

                             }

                             return null;

                         }
                     })
                     .filter(proctorStatisticsReport -> proctorStatistic != null)
                     .toArray(ProctorStatisticsReport[]::new);

    }

    /**
     * ${@inheritDoc}
     */
    public ProctorStatisticsReport[] getAllStatistics() {

        return Arrays.stream(ProctorStatistic.values())
                     .map(proctorStatistic -> getStatisticsFor(proctorStatistic))
                     .flatMap(Stream::of)
                     .toArray(ProctorStatisticsReport[]::new);

    }

}

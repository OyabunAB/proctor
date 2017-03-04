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
import se.oyabun.proctor.statistics.ProctorStatisticType;
import se.oyabun.proctor.statistics.ProctorStatisticsGatherer;
import se.oyabun.proctor.statistics.ProctorStatisticsReport;

import java.util.Arrays;
import java.util.Objects;
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
    ProctorStatisticsGatherer[] getProctorStatisticsGatherersFor(final ProctorStatisticType proctorStatisticType) {

        return Arrays.stream(proctorStatisticsGatherers)
                     .filter(gatherer -> gatherer.gathers(proctorStatisticType))
                     .toArray(ProctorStatisticsGatherer[]::new);

    }

    /**
     * ${@inheritDoc}
     */
    public ProctorStatisticsReport[] getStatisticsFor(final ProctorStatisticType proctorStatisticType) {

        return Arrays.stream(getProctorStatisticsGatherersFor(proctorStatisticType))
                     .map(gatherer -> {
                         try {
                             return new ProctorStatisticsReport(proctorStatisticType,
                                                                gatherer.getMeanFor(proctorStatisticType),
                                                                gatherer.getCountFor(proctorStatisticType),
                                                                gatherer.getFifteenMinuteRateFor(proctorStatisticType),
                                                                gatherer.getFiveMinuteRateFor(proctorStatisticType),
                                                                gatherer.getOneMinuteRateFor(proctorStatisticType));
                         } catch (NonGatheredStatisticRequestException e) {

                             if (logger.isErrorEnabled()) {

                                 logger.error("Failed to fetch statistics for '" + proctorStatisticType + "'.",
                                              e);

                             }

                             return null;

                         }
                     })
                     .filter(Objects::nonNull)
                     .toArray(ProctorStatisticsReport[]::new);

    }

    /**
     * ${@inheritDoc}
     */
    public ProctorStatisticsReport[] getAllStatistics() {

        return Arrays.stream(ProctorStatisticType.values())
                     .map(proctorStatistic -> getStatisticsFor(proctorStatistic))
                     .flatMap(Stream::of)
                     .toArray(ProctorStatisticsReport[]::new);

    }

}

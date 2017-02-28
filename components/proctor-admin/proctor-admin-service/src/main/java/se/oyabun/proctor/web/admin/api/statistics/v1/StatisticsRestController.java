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
package se.oyabun.proctor.web.admin.api.statistics.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import se.oyabun.proctor.statistics.ProctorStatisticType;
import se.oyabun.proctor.statistics.ProctorStatisticsReport;
import se.oyabun.proctor.statistics.manager.ProctorStatisticsManager;

import java.util.Arrays;

/**
 * Proctor Proxy Statistics REST API
 *
 * @version 1
 */
@RestController
@RequestMapping(StatisticsRestController.STATISTICS_ROOT)
public class StatisticsRestController {

    public static final String STATISTICS_ROOT = "/api/v1/statistics";
    public static final String REPORTS = "/reports";
    public static final String STATISTIC_TYPES = "/statistictypes";
    public static final String STATISTIC_PROPERTY = "statisticstype";

    private ProctorStatisticsManager proctorStatisticsManager;

    @Autowired
    public StatisticsRestController(final ProctorStatisticsManager proctorStatisticsManager) {

        this.proctorStatisticsManager = proctorStatisticsManager;

    }

    /**
     * Get statistic report for specific type
     *
     * @param statisticType to fetch report for
     * @return report for given statistic type
     */
    @RequestMapping(value = REPORTS + STATISTIC_TYPES +
                            "/{" + STATISTIC_PROPERTY + "}",
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProctorStatisticsReport[]> getStatisticTypes(@PathVariable(value = STATISTIC_PROPERTY)
                                                                       final ProctorStatisticType statisticType) {

        final ProctorStatisticsReport[] statisticsReports =
                proctorStatisticsManager.getStatisticsFor(statisticType);

        return !Arrays.asList(statisticsReports).isEmpty() ?
               ResponseEntity.status(HttpStatus.OK).body(statisticsReports) :
               ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ProctorStatisticsReport[0]);

    }

    /**
     * Get types of statistics available
     *
     * @return all possible report types
     */
    @RequestMapping(value = STATISTIC_TYPES,
                    method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProctorStatisticType[]> getReportTypes() {

        return !Arrays.asList(ProctorStatisticType.values()).isEmpty() ?
               ResponseEntity.status(HttpStatus.OK).body(ProctorStatisticType.values()) :
               ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ProctorStatisticType[0]);

    }

}

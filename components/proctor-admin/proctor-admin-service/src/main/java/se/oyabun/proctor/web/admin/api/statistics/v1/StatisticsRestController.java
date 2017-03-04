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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.oyabun.proctor.ProctorServerConfiguration;
import se.oyabun.proctor.statistics.ProctorStatisticType;
import se.oyabun.proctor.statistics.ProctorStatisticsReport;
import se.oyabun.proctor.statistics.manager.ProctorStatisticsManager;

import java.util.Arrays;

/**
 * Proctor Proxy Statistics REST API
 * Node specific statistics API, only replies to configured node ID
 *
 * @version 1
 */
@RestController
@RequestMapping(StatisticsRestController.STATISTICS_ROOT)
public class StatisticsRestController {

    public static final String NODEID_PROPERTY = "ID";
    public static final String STATISTICS_ROOT = "/api/v1/cluster/nodes/{" + NODEID_PROPERTY + "}/statistics";
    public static final String REPORTS = "/reports";
    public static final String STATISTIC_TYPES = "/types";
    public static final String STATISTIC_PROPERTY = "TYPE";

    private ProctorServerConfiguration localConfig;

    private ProctorStatisticsManager proctorStatisticsManager;

    @Autowired
    public StatisticsRestController(final ProctorServerConfiguration localConfig,
                                    final ProctorStatisticsManager proctorStatisticsManager) {

        this.localConfig = localConfig;
        this.proctorStatisticsManager = proctorStatisticsManager;

    }

    /**
     * Get statistic report for specific type
     *
     * @param clusterNodeID verifying that the request is for this node
     * @param statisticType to fetch report for
     * @return report for given statistic type
     */
    @RequestMapping(value = REPORTS + "/{" + STATISTIC_PROPERTY + "}",
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProctorStatisticsReport[]> getStatisticTypes(@PathVariable(NODEID_PROPERTY)
                                                                       final String clusterNodeID,
                                                                       @PathVariable(STATISTIC_PROPERTY)
                                                                       final ProctorStatisticType statisticType) {

        if(localConfig.getNodeID().equals(clusterNodeID)) {

            final ProctorStatisticsReport[] statisticsReports =
                    proctorStatisticsManager.getStatisticsFor(statisticType);

            return !Arrays.asList(statisticsReports).isEmpty() ?
                   ResponseEntity.status(HttpStatus.OK).body(statisticsReports) :
                   ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ProctorStatisticsReport[0]);

        } else {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        }

    }

    /**
     * Get types of statistics available
     *
     * @param clusterNodeID of this node (otherwise bad request)
     * @return all possible report types
     */
    @RequestMapping(value = STATISTIC_TYPES,
                    method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProctorStatisticType[]> getStatisticTypes(@PathVariable(NODEID_PROPERTY)
                                                                    final String clusterNodeID) {

        if(localConfig.getNodeID().equals(clusterNodeID)) {

            return !Arrays.asList(ProctorStatisticType.values()).isEmpty() ?
                   ResponseEntity.status(HttpStatus.OK).body(ProctorStatisticType.values()) :
                   ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ProctorStatisticType[0]);

        } else {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(null);

        }

    }

}

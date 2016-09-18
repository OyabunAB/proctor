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
package se.oyabun.proctor.web.admin.api.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.oyabun.proctor.statistics.ProctorStatistic;
import se.oyabun.proctor.statistics.ProctorStatisticsReport;
import se.oyabun.proctor.statistics.manager.ProctorStatisticsManager;
import se.oyabun.proctor.web.admin.api.AbstractSecuredAPIController;

/**
 * Proctor Proxy Statistics REST API
 * @version 1
 */
@RestController
@RequestMapping(value = "/api/1/statistics")
public class StatisticsRestController1
        extends AbstractSecuredAPIController {

    @Autowired
    private ProctorStatisticsManager proctorStatisticsManager;

    @RequestMapping(
            value = "/",
            method = RequestMethod.GET)
    public ResponseEntity<ProctorStatisticsReport[]> getProctorStatisticReportFor(@RequestParam(required = false)
                                                                                      final String proctorStatistic) {
        return ResponseEntity.ok(
                ProctorStatistic.matchesAny(proctorStatistic.toUpperCase()) ?
                        proctorStatisticsManager.getStatisticsFor(
                                ProctorStatistic.valueOf(proctorStatistic.toUpperCase())) :
                        proctorStatisticsManager.getAllStatistics());

    }

}

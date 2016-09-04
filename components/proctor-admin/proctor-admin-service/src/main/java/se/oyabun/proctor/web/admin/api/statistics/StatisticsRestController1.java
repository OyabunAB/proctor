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

/**
 * Proctor Proxy Statistics REST API
 * @version 1
 */
@RestController
@RequestMapping(value = "/api/1/statistics")
public class StatisticsRestController1 {

    @Autowired
    private ProctorStatisticsManager proctorStatisticsManager;

    @RequestMapping(
            value = "/",
            method = RequestMethod.GET)
    public ResponseEntity<ProctorStatisticsReport[]> getProctorStatisticReportFor(@RequestParam(required = false)
                                                                                      final String proctorStatistic) {
        return ResponseEntity.ok(
                ProctorStatistic.matchesAny(proctorStatistic.toUpperCase()) ?
                        new ProctorStatisticsReport[]{
                            proctorStatisticsManager.getStatisticsFor(ProctorStatistic.valueOf(proctorStatistic.toUpperCase()))
                        } :
                        proctorStatisticsManager.getAllStatistics().toArray(new ProctorStatisticsReport[]{}));

    }

}

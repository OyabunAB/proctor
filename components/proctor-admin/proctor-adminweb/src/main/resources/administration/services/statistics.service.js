/**
 *
 * Copyright 2016 Oyabun AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
**/
(function() {

    'use strict';

    var nodeProperty = '{ID}';
    var statisticTypeProperty = '{TYPE}';
    var statisticTypesUrl = 'api/v1/cluster/nodes/' + nodeProperty +
                            '/statistics/types';
    var statisticsReportUrl = 'api/v1/cluster/nodes/' + nodeProperty +
                              '/statistics/reports/' + statisticTypeProperty;

    angular.module('proctoradmin')
           .factory('proctorStatistics', proctorStatistics);

    function proctorStatistics($http,
                               proctorLogger,
                               proctorSecurity) {

        var service = {};
        service.getStatisticTypes = getStatisticTypes;
        service.getStatisticsReport = getStatisticsReport;
        return service;

        function getStatisticTypes(node, callback) {

                proctorLogger.debug('Requesting cluster nodes.');

                var successfulStatisticTypesRequest =
                    function(response) {

                        proctorLogger.debug('Received statistic types data.');

                        callback(response.data);

                    };

                var failedStatisticTypesRequest =
                    function(response) {

                        proctorLogger.debug('Failed to receive statistic types data.');

                        if(response.status === 401) {

                            proctorSecurity.renewAccessToken(function(result) {

                                if(result) {

                                    getStatisticTypes(node, callback);

                                } else {

                                    $state.go('login');

                                }

                            });

                        }

                        callback();

                    };

                $http.get(statisticTypesUrl.replace(nodeProperty, node))
                     .then(successfulStatisticTypesRequest,
                           failedStatisticTypesRequest);

        }

        function getStatisticsReport(node,
                                     type,
                                     callback) {

            var successfulStatisticReportRequest =
                function(response) {

                    proctorLogger.debug('Received statistics report data.');

                    callback(response.data);

                };

            var failedStatisticReportRequest =
                function(response) {

                    proctorLogger.debug('Failed to receive statistics report data.');

                    if(response.status === 401) {

                        proctorSecurity.renewAccessToken(function(result) {

                            if(result) {

                                getStatisticsReport(node, type, callback);

                            } else {

                                $state.go('login');

                            }

                        });

                    }

                    callback();

                };

            $http.get(statisticsReportUrl.replace(nodeProperty, node)
                                         .replace(statisticTypeProperty, type))
                 .then(successfulStatisticReportRequest,
                       failedStatisticReportRequest);

        }

    }


})();
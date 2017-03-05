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

    angular.module('proctoradmin')
           .controller('StatisticsController', statisticsController);

    function statisticsController($scope,
                                  $interval,
                                  proctorCluster,
                                  proctorStatistics,
                                  proctorLogger) {

        var model = this;
        model.loading = [];
        model.nodes = [];
        model.statisticTypes = [];
        model.statisticReports = [];
        model.updateNodes = updateNodes;
        model.updateStatisticTypes = updateStatisticTypes;
        model.getStatisticTypes = getStatisticTypes;
        model.updateStatisticReport = updateStatisticReport;
        model.getAggregateStatisticsReport = getAggregateStatisticsReport;
        model.getStatisticsReport = getStatisticsReport;
        model.updateCharts = updateCharts;

        model.requestReplyGraph =
            {
                config: { title: 'Request reply ratio',
                          units: 'requests'},
                donutConfig: { chartId: 'requestReplyRatioChart',
                               thresholds: {'warning':'5','error':'10'} },
                data: { dataAvailable: false,
                        used: 0,
                        total: 0 },
                centerLabel: 'percent'

            };

        model.handlerHitGraph =
            {
               config: { title: 'Handler match ratio',
                         units: 'hits'},
               donutConfig: { chartId: 'handlerHitChart',
                              thresholds: {'warning':'5','error':'10'} },
               data: { dataAvailable: false,
                       used: 0,
                       total: 0 },
               centerLabel: 'percent'

            }



        initController();

        /**
         * Initialize the controller, setting up intervals and preparing
         * to destroy them on scope destruction.
         */
        function initController() {

            proctorLogger.debug('Initiating statistics controller.');

            //
            // Do first update
            //
            model.updateNodes();

            //
            // Instantiate interval updaters
            //
            var updateNodesPromise =
                $interval( function(){ model.updateNodes(); }, 5000);

            var updateStatisticTypesPromise =
                $interval( function(){ model.updateStatisticTypes(); }, 5000);

            var updateChartsPromise =
                $interval( function(){ model.updateCharts(); }, 1000);
            //
            // Register callbacks to cancel intervals on destroy
            //
            $scope.$on('$destroy',function() {

                if(updateNodesPromise) $interval.cancel(updateNodesPromise);
                if(updateStatisticTypesPromise) $interval.cancel(updateStatisticTypesPromise);
                if(updateCharts) $interval.cancel(updateCharts);

            });

        }

        function updateCharts() {

            var aggregateProxyRequestReport = getAggregateStatisticsReport('PROXY_REQUEST_RECEIVED');
            var aggregateProxyReplyReport = getAggregateStatisticsReport('PROXY_REPLY_SENT');
            var aggreagetProxyHandlerHitReport =

            model.requestReplyGraph.data.total = aggregateProxyReplyReport.aggregatedCountValue;
            model.requestReplyGraph.data.used = aggregateProxyRequestReport.aggregatedCountValue -
                                                aggregateProxyReplyReport.aggregatedCountValue;
            model.requestReplyGraph.data.dataAvailable = true;
        }

        /**
         * Request an update of statistic types
         */
        function updateStatisticTypes() {

            proctorLogger.debug('Requesting update of statistic types.');

            var callback =
                function(types) {

                    if(types) {

                        types.forEach(function(type) {

                            if(!model.statisticTypes.includes(type)) {

                                model.statisticTypes.push(type);

                            }

                        });

                    }


                };

            if(model.nodes) {

                model.nodes.forEach(function(node) {

                    proctorStatistics.getStatisticTypes(node.nodeID, callback);

                });

            }

        }

        /**
         * Get current loaded statistic types
         */
        function getStatisticTypes() {

            proctorLogger.debug('Get statistic types.');

            return model.statisticTypes;

        }

        /**
         * Request and update of a specific statistic report by type
         */
        function updateStatisticReport(nodeID, statisticType) {

            proctorLogger.debug('Requesting update of statistic report for '+statisticType+'.');

            var reportUpdateCallback = function(result) {

                if(result) {

                    if(!model.statisticReports[nodeID]) {

                        model.statisticReports[nodeID] = {};

                    }

                    model.statisticReports[nodeID][statisticType] = result;

                }

            };

            proctorStatistics.getStatisticsReport(nodeID,
                                                  statisticType,
                                                  reportUpdateCallback);

        }

        /**
         * Get aggregated statistics report for type (combine all nodes)
         */
        function getAggregateStatisticsReport(statisticType) {

            proctorLogger.debug('Get aggregated statistic report for '+statisticType+'.');

            var statisticReportsCount = 0;
            for (var nodeReports in model.statisticReports) {
                if (nodeReports.hasOwnProperty(statisticType)) {
                   ++count;
                }
            }

            function sumAggregated(selectedElement) {

                var sum = 0;
                var reportCount=0;

                for(var node in model.statisticReports) {

                    var report = model.statisticReports[node][statisticType];

                    if(report) {

                        for(var element in report[0]) {

                            if(report[0].hasOwnProperty(element) &&
                               element === selectedElement) {

                                ++reportCount;

                                var parsedNumber = parseFloat( report[0][element] );

                                sum += parsedNumber ? parsedNumber : 0;

                            }

                        }

                    }

                }

                return sum/reportCount;

            }


            return {

                aggregatedCountValue: sumAggregated('countValue'),
                aggregatedFifteenMinuteRateValue: sumAggregated('fifteenMinuteRateValue'),
                aggregatedFiveMinuteRateValue: sumAggregated('fiveMinuteRateValue'),
                aggregatedMeanValue: sumAggregated('meanValue'),
                aggregatedOneMinuteRateValue: sumAggregated('oneMinuteRateValue'),
                proctorStatisticType: statisticType

            };

        }

        function getStatisticsReport(nodeID, statisticType) {

            var nodeReports = model.statisticReports[nodeID];

            if(nodeReports) {

                var nodeReport = nodeReports[statisticType];

                if(nodeReport) {

                    return nodeReport;

                }

            }

            return null;

        }

        /**
         * Request an update of cluster nodes
         */
        function updateNodes() {

            var clusterRequestCallback =
                function(nodes) {

                    if(nodes) {

                        model.nodes = nodes;

                        model.nodes.forEach(
                            function(node, nodeIndex) {

                                model.statisticTypes.forEach(
                                    function(type, typeIndex) {

                                        updateStatisticReport(node.nodeID, type);

                                    });

                            });

                    } else {

                        model.nodes = [];

                    }


                };

            proctorCluster.getNodes(clusterRequestCallback);

        }

    }

})();


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

    var nodesUrl = 'api/v1/clusters/nodes';

    angular.module('proctoradmin')
           .factory('proctorCluster', proctorCluster);

    function proctorCluster($http,
                            $localStorage,
                            proctorLogger,
                            proctorSecurity) {

        var service = {};
        service.getNodes = getNodes;
        return service;

        function getNodes(callback) {

            proctorLogger.debug('Requesting cluster nodes.');

            var successfulNodesRequest =
                function(response) {

                    proctorLogger.debug('Received cluster node data.');

                    callback(response.data);

                };

            var failedNodesRequest =
                function(response) {

                    proctorLogger.debug('Failed to receive cluster node data.');

                    if(response.status === 401) {

                        proctorSecurity.renewAccessToken(function(result) {

                            if(result) {

                                getNodes(callback);

                            } else {

                                $state.go('login');

                            }

                        });

                    }
                    callback(null);

                };

            $http.get(nodesUrl)
                 .then(successfulNodesRequest, failedNodesRequest);

        }


    }

})();
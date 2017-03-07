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

    var handlerTypesUrl = 'api/v1/handlers/handlertypes';
    var handlerConfigurationsUrl = 'api/v1/handlers/configurations'

    angular.module('proctoradmin')
           .factory('proctorHandler', proctorHandler);

    function proctorHandler($http,
                            proctorLogger,
                            proctorSecurity,
                            proctorUUID) {

        var service = {};
        service.getHandlerTypes = getHandlerTypes;
        service.getHandlerConfigurationIDs = getHandlerConfigurationIDs;
        service.getHandlerConfiguration = getHandlerConfiguration;
        service.addHandlerConfiguration = addHandlerConfiguration;
        return service;

        function addHandlerConfiguration(handlerConfiguration,
                                         callback) {

            var UUID = proctorUUID.generateUUID();

            handlerConfiguration.configurationID = UUID

            proctorLogger.debug('Adding handler configuration '+UUID+'.');

            var successfulHandlerConfigurationAdditionRequest =
                function(result) {

                    proctorLogger.debug('Added handler configuration.');

                    callback(true);

                };

            var failedHandlerConfigurationAdditionRequest =
                function(result) {

                    proctorLogger.debug('Failed to add handler configuration.');

                    if(response.status === 401) {

                        proctorSecurity.renewAccessToken(function(result) {
                            if(result) {

                                addHandlerConfiguration(handlerConfiguration,
                                                        callback);

                            } else {

                                $state.go('login');

                            }

                        });

                    }

                    callback(false);

                };

            $http({
                url: handlerConfigurationsUrl,
                method: 'POST',
                data: handlerConfiguration,
                headers: {
                    'Content-Type': 'application/json;charset=UTF-8',
                    'Accept': 'application/json'
                }
            })
            .then(successfulHandlerConfigurationAdditionRequest,
                  failedHandlerConfigurationAdditionRequest);

        }

        /**
         * Requests handler configuration IDs from API and returns them in callback
         */
        function getHandlerConfigurationIDs(callback) {

            proctorLogger.debug('Requesting handler configuration IDs.');

            var successfulHandlerConfigurationIDsRequest =
                function(response) {

                    proctorLogger.debug('Received handler configuration IDs data.');

                    callback(response.data);

                };

            var failedHandlerConfigurationIDsRequest =
                function(response) {

                proctorLogger.debug('Failed to receive handler configuration IDs data.');

                if(response.status === 401) {

                    proctorSecurity.renewAccessToken(function(result) {
                        if(result) {

                            getHandlerConfigurationIDs(callback);

                        } else {

                            $state.go('login');

                        }

                    });

                }

                callback(null);

            };

            $http.get(handlerConfigurationsUrl)
                 .then(successfulHandlerConfigurationIDsRequest,
                       failedHandlerConfigurationIDsRequest);


        }

        /**
         * Request handler configuration by ID
         */
        function getHandlerConfiguration(handlerConfigurationID, callback) {

            proctorLogger.debug('Requesting handler configuration for '+handlerConfigurationID+'.');

            var successfulHandlerConfigurationIDsRequest =
                function(response) {

                    proctorLogger.debug('Received handler configuration data.');

                    callback(response.data);

                };

            var failedHandlerConfigurationIDsRequest =
                function(response) {

                proctorLogger.debug('Failed to receive handler configuration data.');

                if(response.status === 401) {

                    proctorSecurity.renewAccessToken(function(result) {
                        if(result) {

                            getHandlerTypes(handlerConfigurationID, callback);

                        } else {

                            $state.go('login');

                        }

                    });

                }

                callback(null);

            };

            $http.get(handlerConfigurationsUrl+'/'+handlerConfigurationID+'/')
                 .then(successfulHandlerConfigurationIDsRequest,
                       failedHandlerConfigurationIDsRequest);

        }

        /**
         * Requests handler types from API and returns them in callback
         */
        function getHandlerTypes(callback) {

            proctorLogger.debug('Requesting handler types.');

            var successfulHandlerTypesRequest =
                function(response) {

                    proctorLogger.debug('Received handler types data.');

                    callback(response.data);

                };

            var failedHandlerTypesRequest =
                function(response) {

                proctorLogger.debug('Failed to receive handler types data.');

                if(response.status === 401) {

                    proctorSecurity.renewAccessToken(function(result) {
                        if(result) {

                            getHandlerTypes(callback);

                        } else {

                            $state.go('login');

                        }

                    });

                }

                callback();

            };

            $http.get(handlerTypesUrl)
                 .then(successfulHandlerTypesRequest,
                       failedHandlerTypesRequest);

        }

    }

})();
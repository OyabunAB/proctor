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
        .controller('HandlersController', handlersController);

    function handlersController($scope,
                                $interval,
                                proctorHandler,
                                proctorLogger) {



        var model = this;
        model.loading = [];
        model.handlerTypes = [];
        model.handlerConfigurationIDs = [];
        model.handlerConfigurations = [];
        model.isLoading = isLoading;
        model.updateHandlerTypes = updateHandlerTypes;
        model.updateHandlerConfigurationIDs = updateHandlerConfigurationIDs;
        model.getHandlerConfiguration = getHandlerConfiguration;
        model.getHandlerConfigurations = getHandlerConfigurations;

        initController();

        /**
         * Initialize the controller, setting up intervals and preparing
         * to destroy them on scope destruction.
         */
        function initController() {

            proctorLogger.debug('Initiating handlers controller.');

            //
            // Do first update
            //
            model.updateHandlerTypes();
            model.updateHandlerConfigurationIDs();

            //
            // Instantiate interval updaters
            //
            var handlerTypePromise = $interval( function(){ model.updateHandlerTypes(); }, 5000);
            var handlerConfigurationIDsPromise = $interval( function(){ model.updateHandlerConfigurationIDs(); }, 5000);

            //
            // Register callbacks to cancel intervals on destroy
            //
            $scope.$on('$destroy', function() {

                proctorLogger.debug('Cancelling handlers controller updaters.');

                if(handlerTypePromise) $interval.cancel(handlerTypePromise);
                if(handlerConfigurationIDsPromise) $interval.cancel(handlerConfigurationIDsPromise);

            });

        }

        /**
         *  Request an update of current handler configuration IDs
         */
        function updateHandlerConfigurationIDs() {

            proctorLogger.debug('Updating handler configuration IDs.');

            var callback =
                function(handlerConfigurationIDs) {

                    if(handlerConfigurationIDs) {

                        model.handlerConfigurationIDs = handlerConfigurationIDs;

                        model.handlerConfigurationIDs.forEach(
                            function(handlerConfigurationID, index) {

                                model.getHandlerConfiguration(handlerConfigurationID);

                            });

                    } else {

                        model.handlerConfigurationIDs = [];

                    }

                };

            proctorHandler.getHandlerConfigurationIDs(callback);

        }

        /**
         * Request an update of current handler types
         */
        function updateHandlerTypes() {

            proctorLogger.debug('Updating handler types.');

            var callback =
                function(handlerTypes) {

                    if(handlerTypes) {

                        model.handlerTypes = handlerTypes;

                    } else {

                        model.handlerTypes = [];

                    }

                };

            proctorHandler.getHandlerTypes(callback);

        }

        function isLoading(handlerConfigurationID) {

            var isloading = model.loading[handlerConfigurationID];

            return isloading ? true : false;

        }

        function getHandlerConfigurations() {

            var handlerConfigurationsArray = [];

            for (var key in model.handlerConfigurations) {

              if (model.handlerConfigurations.hasOwnProperty(key)) {

                handlerConfigurationsArray.push(model.handlerConfigurations[key]);

              }

            }

            return handlerConfigurationsArray;

        }

        /**
         * Get a specific handler configuration by ID
         */
        function getHandlerConfiguration(handlerConfigurationID) {

            proctorLogger.debug('Getting specific handler configuration for ID '+handlerConfigurationID+'.');

            var callback =
                function(result) {

                    if(result) {

                        model.handlerConfigurations[handlerConfigurationID] = result;

                        model.loading[handlerConfigurationID] = false;

                    } else {

                        model.handlerConfigurations[handlerConfigurationID] = {};

                        model.loading[handlerConfigurationID] = false;

                    }

                };

            model.loading[handlerConfigurationID] = true;

            proctorHandler.getHandlerConfiguration(handlerConfigurationID, callback)

        }

    }

})();

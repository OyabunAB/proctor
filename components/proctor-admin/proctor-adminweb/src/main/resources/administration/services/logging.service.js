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
           .provider('proctorLoglevel', proctorLoglevelProvider)
           .service('proctorLogger', proctorLogger);

    /**
     * Proctor logger service
     */
    function proctorLogger(proctorLoglevel) {

        var service = {};

        service.isDebugEnabled = isDebugEnabled;
        service.debug = debug;

        return service;

        function isDebugEnabled() {

            return proctorLoglevel.level === 'debug';

        }

        function debug(message) {

            if(isDebugEnabled) {

                console.debug(message);

            }

        }

    }

    /**
     * Proctor loglevel provider
     */
    function proctorLoglevelProvider() {

        var globalConfig = {};

        this.config =
            function(value) {
                globalConfig = value;
            };

        this.$get = function() {

            var options = {
                level: null
            };

            function ProctorLoglevel() {

                var config =
                    this.config =
                        angular.extend({}, options, globalConfig);

            }

            ProctorLoglevel.prototype.getConfig = function() {
                return this.config;
            };

            return new ProctorLoglevel();

        }

    };


})();
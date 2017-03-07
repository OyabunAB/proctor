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
           .factory('proctorUUID', proctorUUID);

    function proctorUUID() {

        var service = this;
        service.generateUUID = generateUUID;
        service.generateGUID = generateGUID;
        return service;

        function s4() {

            return Math.floor((1 + Math.random()) * 0x10000)
                       .toString(16)
                       .substring(1);

        }

        /**
         * http://www.ietf.org/rfc/rfc4122.txt
         */
        function generateUUID() {


            var s = [];

            var hexDigits = "0123456789abcdef";

            for (var i = 0; i < 36; i++) {

                s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);

            }

            //
            // bits 12-15 of the time_hi_and_version field to 0010
            //
            s[14] = "4";

            //
            // bits 6-7 of the clock_seq_hi_and_reserved to 01
            //
            s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1);

            s[8] = s[13] = s[18] = s[23] = "-";

            return s.join("");

        }

        function generateGUID() {

            return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
                   s4() + '-' + s4() + s4() + s4();

        }

    }

})();

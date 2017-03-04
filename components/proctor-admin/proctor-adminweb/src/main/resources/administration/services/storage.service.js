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
           .factory('proctorStorage', proctorStorage);

    function proctorStorage($localStorage) {

        var service = {};

        service.setCurrentUser = setCurrentUser;
        service.getCurrentUser = getCurrentUser;
        service.deleteCurrentUser = deleteCurrentUser;

        service.setAuthenticationToken = setAuthenticationToken;
        service.getAuthenticationToken = getAuthenticationToken;
        service.deleteAuthenticationToken = deleteAuthenticationToken;

        service.setAccessToken = setAccessToken;
        service.getAccessToken = getAccessToken;
        service.deleteAccessToken = deleteAccessToken;

        return service;

        function getCurrentUser() {

            return $localStorage.currentUser;

        }

        function setCurrentUser(currentUser) {

            $localStorage.currentUser = currentUser;

        }

        function deleteCurrentUser() {

            delete $localStorage.currentUser;

        }

        function getAuthenticationToken() {

            return $localStorage.authenticationToken;

        }

        function setAuthenticationToken(authenticationToken) {

            $localStorage.authenticationToken = authenticationToken;

        }

        function deleteAuthenticationToken() {

            delete $localStorage.authenticationToken;

        }

        function getAccessToken() {

            return $localStorage.accessToken;

        }

        function setAccessToken(accessToken) {

            $localStorage.accessToken = accessToken;

        }

        function deleteAccessToken() {

            delete $localStorage.accessToken;

        }

    }

})();
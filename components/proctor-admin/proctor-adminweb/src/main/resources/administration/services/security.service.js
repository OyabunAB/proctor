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

    var authorizationUrl = 'security/v1/authorization/tokens';
    var accessUrl        = 'security/v1/access/tokens';

    angular.module('proctoradmin')
           .factory('proctorSecurity', proctorSecurity);

    /**
     * Definition of proctorSecurity
     */
    function proctorSecurity($http,
                             $state,
                             proctorLogger,
                             proctorStorage,
                             jwtHelper,
                             md5) {

        var service = {};
        service.loadingAccesstoken = false;
        service.login = login;
        service.logout = logout;
        service.getCurrentUser = getCurrentUser;
        service.getAccessToken = getAccessToken;
        service.renewAccessToken = renewAccessToken;
        service.isAuthenticated = isAuthenticated;
        return service;

        /**
         * Try to get an authorization token
         */
        function login(username,
                       password,
                       callback) {

            proctorLogger.debug('Requesting authorization for '+username+'.');

            var passwordHash = md5.createHash(password || '');

            var userRequestBody = { 'username': username, 'password': passwordHash };

            var successfulAuthorization =
                function (response) {

                    //
                    // Login successful if there's a token in the response
                    //
                    if (response.data.signedContent) {

                        var tokenPayload = jwtHelper.decodeToken(response.data.signedContent);

                        //
                        // Store parsed content and complete token wrapper for user
                        //
                        proctorStorage.setCurrentUser(tokenPayload);

                        proctorStorage.setAuthenticationToken(response.data);

                        proctorLogger.debug('Successfully received ' + response.data.type +
                                            ' token for ' + response.data.username +
                                            ', expiring ' + response.data.expires + '.');

                        //
                        // Execute callback with true to indicate successful login
                        //
                        callback(true);

                    } else {

                        proctorLogger.debug('Authorization response not containing correct content.');

                        //
                        // Execute callback with false to indicate failed login
                        //
                        callback(false);

                    }

                };

            var failedAuthorization =
                function (response) {

                    proctorLogger.debug('Authorization request failed.');

                    //
                    // Execute callback with false to indicate failed login
                    //
                    callback(false);

                };

            //
            // Do http post for auth token
            //
            $http.post(authorizationUrl, userRequestBody)
                 .then(successfulAuthorization, failedAuthorization);

        }

        /**
         * Verify authentication data
         */
        function isAuthenticated() {

             proctorLogger.debug('Verifying authentication.');

            if(proctorStorage.getAuthenticationToken()) {

                if(jwtHelper.isTokenExpired(
                    proctorStorage.getAuthenticationToken().signedContent)) {

                    return false;

                } else {

                    return true;

                }

            } else {

                return false;
            }

        }

        /**
         * Get the content of access token (renewing if needed)
         */
        function getAccessToken() {

            var accessToken = proctorStorage.getAccessToken();

            if(accessToken) {

                return accessToken.signedContent;

            } else {

                return null;

            }

        }

        /**
         * Renews access token with current authorization token (if present)
         */
        function renewAccessToken(callback) {

            //
            // Define callback for successful access token exchange
            //
            var successfulExchange =
                function (response) {

                    service.loadingAccesstoken = false;

                    if(response.data.signedContent) {

                        proctorLogger.debug('Successfully received ' + response.data.type +
                                            ' token for ' + response.data.username +
                                            ', expiring ' + response.data.expires + '.');


                        proctorStorage.setAccessToken(response.data);

                        callback(true);

                    } else {

                        proctorLogger.debug('Renew access token responded with faulty data.');

                        callback(false);

                    }

                };

            //
            // Define callback for failed access token exchange
            //
            var failedExchange =
                function (response) {

                    proctorLogger.debug('Renew access token request failed.');

                    service.loadingAccesstoken = false;

                    callback(false);

                };

            if(proctorStorage.getAuthenticationToken()) {

                proctorLogger.debug('Requesting new access token with authorization.');

                if(!service.loadingAccesstoken) {

                    service.loadingAccesstoken = true;

                    $http.post(accessUrl, proctorStorage.getAuthenticationToken())
                         .then(successfulExchange, failedExchange);

                } else {

                    proctorLogger.debug('Waiting for response, skipping renew attempt.');

                }


            } else {

                proctorLogger.debug('No authorization, skipping renew attempt.');

                callback(false);
            }

        }

        /**
         * Clear current security information
         */
        function logout(nextState) {

            proctorLogger.debug('Clearing security context.');

            //
            // Remove user security objects from local storage and clear http auth header
            //
            proctorStorage.deleteCurrentUser();
            proctorStorage.deleteAccessToken();
            proctorStorage.deleteAuthenticationToken();

            if(nextState) {

                $state.go(nextState);

            } else {

                $state.go('login');

            }

        }

        /**
         * Get current user
         */
        function getCurrentUser() {

            return proctorStorage.getCurrentUser();

        }

    }

})();
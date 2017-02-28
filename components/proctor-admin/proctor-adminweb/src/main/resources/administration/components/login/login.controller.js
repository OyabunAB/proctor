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
           .controller('LoginController', loginController);

    function loginController($state,
                             proctorLogger,
                             proctorSecurity) {

        var model = this;

        model.login = login;
        model.username = "";
        model.password = "";
        model.error = null;

        initController();

        function initController() {

            proctorLogger.debug('Initializing login controller.');

            //
            // Reset login status
            //
            proctorSecurity.logout();

        };

        function login() {

            proctorLogger.debug('Logging in user with password.');

            model.loading = true;

            proctorSecurity.login(model.username,
                                  model.password,
                                  function (authresult) {

                if (authresult === true) {

                    proctorSecurity.renewAccessToken(function(accessresult) {

                        if(accessresult === true) {

                            proctorLogger.debug('Access request successful, redirecting to dashboard.');

                            $state.go('dashboard');

                        } else {

                            proctorLogger.debug('Failed access request.');

                            model.error = 'Failed to get access';
                            model.loading = false;

                        }

                    });



                } else {

                    proctorLogger.debug('Failed to authorize login.');

                    model.error = 'Username or password is incorrect';
                    model.loading = false;

                }

            });

        };

    };

})();


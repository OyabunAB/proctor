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

    angular.module('proctoradmin',
                   ['ui.router',
                    'ngMessages',
                    'ngStorage',
                    'angular-jwt'])
           .config(config)
           .run(run);

    function config($httpProvider,
                    $stateProvider,
                    $urlRouterProvider,
                    proctorLoglevelProvider,
                    jwtOptionsProvider) {

        proctorLoglevelProvider.config({
            level: 'debug'
        });

        //
        // Configure JWT token handling
        //
        var tokenFunction =
            function(options,
                     proctorSecurity) {

                return proctorSecurity.getAccessToken();

            };

        jwtOptionsProvider.config({
            unauthenticatedRedirectPath: '/login',
            tokenGetter: ['options',
                          'proctorSecurity',
                          tokenFunction]
        });

        $httpProvider.interceptors.push('jwtInterceptor');

        //
        // For any unmatched url, send to /login
        //
        $urlRouterProvider.otherwise("/login")

        $stateProvider.state('login', {
            url: '/login',
            templateUrl: 'administration/components/login/login.view.html',
            controller: 'LoginController',
            controllerAs: 'loginController',
            data: {
                requiresLogin: false
            }
        })

        .state('dashboard', {
            url: '/dashboard',
            templateUrl: 'administration/components/dashboard/dashboard.view.html',
            controller: 'DashboardController',
            controllerAs: 'dashboardController',
            data: {
                requiresLogin: true
            }
        })

        .state('dashboard.handlers', {
            url: '/handlers',
            templateUrl: 'administration/components/dashboard/handlers/handlers.view.html',
            controller: 'HandlersController',
            controllerAs: 'handlersController',
            data: {
                requiresLogin: true
            }
        })

        .state('dashboard.statistics', {
            url: '/statistics',
            templateUrl: 'administration/components/dashboard/statistics/statistics.view.html',
            controller: 'StatisticsController',
            controllerAs: 'statisticsController',
            data: {
                requiresLogin: true
            }
        })

        .state('dashboard.cluster', {
            url: '/cluster',
            templateUrl: 'administration/components/dashboard/cluster/cluster.view.html',
            controller: 'ClusterController',
            controllerAs: 'clusterController',
            data: {
                requiresLogin: true
            }
        });

    };

    function run($rootScope,
                 $state,
                 $http,
                 $location,
                 $localStorage,
                 proctorLogger,
                 proctorSecurity,
                 authManager) {

        //
        // Exchange auth token for access token if it expires
        //
        $rootScope.$on('tokenHasExpired', function(proctorSecurity) {

          if($localStorage.authToken) {

            proctorSecurity.renewAccessToken(function(result) {

                if(result) {

                    proctorLogger.debug('Renewed access token.');

                } else {

                    $state.go('login');

                }

            });

          } else {

            $location = '/login';

          }

        });

        authManager.checkAuthOnRefresh();

    };

})();
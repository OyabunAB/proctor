administrationApplication.config(['$routeProvider',
    function($routeProvider) {
        $routeProvider.
            when('/login', {
            		templateUrl: 'administration/components/login/loginView.html',
                    controller: 'LoginController'
            }).
            when('/dashboard', {
            		templateUrl: 'administration/components/login/dashboardView.html',
                    controller: 'DashboardController'
            }).
            otherwise({
                    redirectTo: '/login'
            });
    }]);
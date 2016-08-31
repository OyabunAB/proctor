administrationApplication.config(

    function($stateProvider,
             $urlRouterProvider){

        //
        // For any unmatched url, send to /login
        //
        $urlRouterProvider.otherwise("/login")

        $stateProvider.state('login', {
            url: '/login',
            templateUrl: 'administration/components/login/loginView.html',
            controller: 'LoginController'
        })

        .state('dashboard', {
            url: '/dashboard',
            templateUrl: 'administration/components/dashboard/dashboardView.html',
            controller: 'DashboardController'
        })

        .state('dashboard.handlers', {
            url: '/handlers',
            templateUrl: 'administration/components/dashboard/handlers/handlersView.html',
            controller: 'HandlersController'
        })

        .state('dashboard.statistics', {
            url: '/statistics',
            templateUrl: 'administration/components/dashboard/statistics/statisticsView.html',
            controller: 'StatisticsController'
        })

        .state('dashboard.cluster', {
            url: '/cluster',
            templateUrl: 'administration/components/dashboard/cluster/clusterView.html',
            controller: 'ClusterController'
        })

});
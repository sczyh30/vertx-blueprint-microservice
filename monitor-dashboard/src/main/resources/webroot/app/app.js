'use strict';

var monitorApp = angular.module('monitorApp', [
  'ngRoute',
  'appControllers',
  'highcharts-ng'
]);

/**
 * Config routes
 */
monitorApp.config(['$routeProvider', function ($routeProvider) {
  $routeProvider.when('/', {
    templateUrl: 'app/view/index.html',
    controller: 'AppIndexCtrl'
  }).when('/metrics', {
    templateUrl: 'app/view/metrics.html',
    controller: 'AppMetricsCtrl'
  }).when('/logs', {
    templateUrl: 'app/view/logs.html',
    controller: 'AppLogCtrl'
  }).when('/orders', {
    templateUrl: 'app/view/orders.html',
    controller: 'AppOrderCtrl'
  }).otherwise({
    redirectTo: '/'
  })
}]);


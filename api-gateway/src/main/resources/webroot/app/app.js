'use strict';

var vertxApp = angular.module('vertxApp', [
  'ngRoute',
  'appControllers'
]);

/**
 * Config routes
 */
vertxApp.config(['$routeProvider', function ($routeProvider) {
  $routeProvider.when('/', {
    templateUrl: 'app/view/home.html',
    controller: 'AppIndexCtrl'
  }).when('/p/:page', {
    templateUrl: 'app/view/home.html',
    controller: 'AppIndexCtrl'
  }).when('/products/:productId', {
    templateUrl: 'app/view/product-detail.html',
    controller: 'ProductDetailCtrl'
  }).when('/cart', {
    templateUrl: 'app/view/cart.html',
    controller: 'CartCtrl'
  }).when('/account', {
    templateUrl: 'app/view/account.html',
    controller: 'AccountCtrl'
  }).when('/orders', {
    templateUrl: 'app/view/orders.html',
    controller: 'UserOrderCtrl'
  }).when('/orders/:orderId', {
    templateUrl: 'app/view/order-detail.html',
    controller: 'OrderDetailCtrl'
  }).when('/404', {
    templateUrl: 'app/view/404.html'
  }).otherwise({
    redirectTo: '/404'
  })
}]);


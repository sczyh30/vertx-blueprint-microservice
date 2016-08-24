'use strict';

var appControllers = angular.module('appControllers', []);

const API_URI = '/api';

vertxApp.controller('AppIndexCtrl', ['$scope', '$http', '$templateCache', '$routeParams',
  function ($scope, $http, $templateCache, $routeParams) {
    $scope.products = [];
    $scope.reqUrl = "/api/product/products";
    if ($routeParams.p) {
      $scope.reqUrl = '/api/product/products?p=' + $routeParams.p;
    } else {
      $scope.reqUrl = '/api/product/products';
    }
    let getProducts = () => {
      $http({
        method: 'GET',
        url: $scope.reqUrl
      }).success((data, status, headers, config) => {
        $scope.products = data;
      });
    };

    getProducts();
  }]);

vertxApp.controller('ProductDetailCtrl', ['$scope', '$routeParams', '$http', '$location',
  function ($scope, $routeParams, $http, $location) {
    $scope.productDetailUrl = API_URI + '/product/' + $routeParams.productId;
    $scope.inventoryUrl = API_URI + '/inventory/' + $routeParams.productId;
    $scope.product = {};
    $scope.inventory = false;

    $scope.$on('logout', (event, msg) => {
      fetchProductDetail();
    });

    var fetchProductDetail = () => {
      $http({
        method: 'GET',
        url: $scope.productDetailUrl
      }).success((data, status, headers, config) => {
        $scope.product = data;
        $scope.product.poster_image = '/assets/img/posters/' + $scope.product.productId + '.jpg';
      }).error((data, status, headers, config) => {
        if (status == 404) {
          $location.path("/404");
          $location.reload($location.path);
        }
      });
      $http({
        method: 'GET',
        url: $scope.inventoryUrl
      }).success((data, status, headers, config) => {
        $scope.inventory = data > 0;
      })
    };

    fetchProductDetail();
  }]);

vertxApp.controller('CartCtrl', ['$scope', '$http', '$templateCache',
  function ($scope, $http) {
    $scope.cartUrl = '/api/cart/cart';
    $scope.cart = {};

    $scope.$on('cartEvents', (event, msg) => {
      fetchCart();
    });

    var fetchCart = function () {
      $http({
        method: 'GET',
        url: $scope.cartUrl
      }).success(data => {
        $scope.cart = data;
        $scope.cart.total = 0;
        $scope.cart.totalItems = 0;
        for (var i = 0; i < $scope.cart.productItems.length; i++) {
          $scope.cart.productItems[i].posterImage = '/assets/img/posters/' + $scope.cart.productItems[i].productId + '.jpg';
          $scope.cart.productItems[i].originalQuantity = $scope.cart.productItems[i].amount;
          $scope.cart.total += $scope.cart.productItems[i].amount * $scope.cart.productItems[i].price;
          $scope.cart.totalItems += $scope.cart.productItems[i].amount;
        }
      }).error(() => {

      });
    };

    fetchCart();
  }]);

vertxApp.controller('AddToCartCtrl', ['$scope', '$http', '$rootScope', function ($scope, $http, $rootScope) {
  $scope.amount = 0;
  $scope.productId = "";
  $scope.addToCart = () => {
    if ($scope.amount && $scope.amount > 0) {
      var req = {
        method: 'POST',
        url: '/api/cart/events',
        headers: {
          'Content-Type': "application/json"
        },
        data: {
          "cartEventType": "ADD_ITEM",
          "productId": $scope.product.productId,
          "amount": $scope.amount,
          "userId": $rootScope.user.id
        }
      };

      $http(req).then(() => {
        $scope.amount = 0;
        function showAlert() {
          $("#addProductAlert").addClass("in");
        }

        function hideAlert() {
          $("#addProductAlert").removeClass("in");
        }

        window.setTimeout(function () {
          showAlert();
          window.setTimeout(function () {
            hideAlert();
          }, 2000);
        }, 20);
      });
    }
  };
}]);

vertxApp.controller('UpdateCartCtrl', ['$rootScope', '$scope', '$http', function ($rootScope, $scope, $http) {
  $scope.productId = "";

  $scope.updateCart = () => {
    let delta = 0;
    if ($scope.item.amount >= 0 && $scope.item.originalQuantity > 0 &&
      $scope.item.amount != $scope.item.originalQuantity) {
      var updateCount = $scope.item.amount - $scope.item.originalQuantity;
      delta = Math.abs(updateCount);
      if (delta > 0) {
        var req = {
          method: 'POST',
          url: '/api/cart/events',
          headers: {
            'Content-Type': "application/json"
          },
          data: {
            "cartEventType": updateCount <= 0 ? "REMOVE_ITEM" : "ADD_ITEM",
            "productId": $scope.item.productId,
            "amount": delta,
            "userId": $rootScope.user.id
          }
        };

        var selector = "#updateProductAlert." + $scope.item.productId;

        $http(req).then(() => {

          if (updateCount <= 0) {
            $rootScope.$broadcast('cartEvents', "update");
          }

          $scope.item.originalQuantity = $scope.item.amount;

          function showAlert() {
            $(selector).find("p").text("Cart updated");
            $(selector).removeClass("alert-error")
              .addClass("alert-success")
              .addClass("in");
          }

          function hideAlert() {
            $(selector).removeClass("in");
          }

          window.setTimeout(function () {
            showAlert();
            window.setTimeout(function () {
              hideAlert();
            }, 2000);
          }, 20);
        });
      }
    } else if (delta < 0) {
      $rootScope.item.amount = $scope.item.originalQuantity;
      if ($scope.item.amount <= 0) {
        $scope.$broadcast('cartEvents', "update");
      }
      window.setTimeout(() => {
        $(selector).find("p").text("Invalid quantity");
        $(selector).removeClass("alert-success")
          .addClass("alert-error")
          .addClass("in");
        window.setTimeout(() => {
          $(selector).removeClass("in");
        }, 2000);
      }, 20);
    }
  };
}]);

vertxApp.controller('CheckoutCtrl', ['$scope', '$http', '$location', function ($scope, $http, $location) {
  $scope.checkout = () => {
    var req = {
      method: 'POST',
      url: '/api/cart/checkout',
      headers: {
        'Content-Type': "application/json"
      },
      data: {}
    };

    $http(req).success((data, status, headers, config) => {
      if (data.order == null) {
        alert(data.message);
      } else {
        $scope.order = data.order;
        $location.path('/orders/' + $scope.order.orderId);
      }
    }).error(() => {
      alert("Checkout failed...");
    });
  };
}]);

vertxApp.controller('HeaderCtrl', ['$scope', '$http', '$rootScope', '$location',
  function ($scope, $http, $rootScope, $location) {
    $scope.authUrl = '/uaa';
    $scope.user = {};
    $rootScope.user = {};

    $scope.logout = function () {
      $http.post('/logout', {}).success(() => {
        $rootScope.authenticated = false;
        $scope.user = {};
        $rootScope.user = {};
        $location.path("/");
        $location.reload($location.path);
        $rootScope.$broadcast('logout', "update");
      }).error(() => {
        $scope.user = {};
        $rootScope.$broadcast('logout', "update");
      });
    };


    var fetchUser = function () {
      $http({
        method: 'GET',
        url: $scope.authUrl
      }).success((data, status, headers, config) => {
        $scope.user = data;
        $rootScope.authenticated = true;
        $rootScope.user = data;
      }).error(() => {
        scope.user = {};
        $rootScope.authenticated = false;
      });
    };

    fetchUser();
  }]);

vertxApp.controller('AccountCtrl', ['$scope', '$http',
  function ($scope, $http) {
    $scope.url = '/uaa';
    $scope.user = {};

    var fetchUser = function () {
      $http({
        method: 'GET',
        url: $scope.url
      }).success(data => {
        $scope.user = data;
      }).error((data, status, headers, config) => {
      });
    };

    fetchUser();
  }]);

vertxApp.controller('OrderDetailCtrl', ['$scope', '$rootScope', '$http', '$location', '$routeParams',
  function ($scope, $rootScope, $http, $location, $routeParams) {
    $scope.orderItemUrl = '/api/order/orders/' + $routeParams.orderId;
    var fetchOrder = () => {
      $http({
        method: 'GET',
        url: $scope.orderItemUrl
      }).success((data, status, headers, config) => {
        $scope.order = data;
      }).error((data, status, headers, config) => {
        if (status == 404) {
          $location.path('/404');
        }
      });
    };
    if ($rootScope.user && $rootScope.user.id) {
      fetchOrder();
    } else {
      $location.path('/404');
    }
  }]);

vertxApp.controller('UserOrderCtrl', ['$scope', '$rootScope', '$http', '$location',
  function ($scope, $rootScope, $http, $location) {
    $scope.orders = [];

    $scope.userOrderURL = '/api/order/user/' + $rootScope.user.id + "/orders";
    var fetchOrders = () => {
      $http({
        method: 'GET',
        url: $scope.userOrderURL
      }).success(data => {
        $scope.orders = data;
      }).error((data, status, headers, config) => {
        // $location.path('/');
      })
    };
    fetchOrders();
  }]);
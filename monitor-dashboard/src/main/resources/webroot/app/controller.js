'use strict';

/**
 * Monitor dashboard controller
 *
 * @author Eric Zhao
 */
var appControllers = angular.module('appControllers', []);

monitorApp.controller('AppIndexCtrl', ['$scope', '$http', '$templateCache',
  function ($scope, $http, $templateCache) {
    $scope.records = [];

    var fetchRegServices = () => {
      $http({
        method: 'GET',
        url: '/discovery',
        cache: $templateCache
      }).success(data => {
        $scope.records = data;
      }).error((data, status, headers, config) => {

      });
    };

    fetchRegServices();

    setInterval(fetchRegServices, 10000);
  }]);

monitorApp.controller('AppMetricsCtrl', ['$scope',
  function ($scope) {
    $scope.metrics = [];

    $scope.ebChartConfig = {
      options: {
        chart: {
          type: 'spline',
          animation: Highcharts.svg
        }
      },
      title: {
        text: "Event bus messages throughput"
      },
      xAxis: {
        type: 'datetime',
        tickPixelInterval: 150
      },
      yAxis: {
        title: {
          text: 'Value'
        },
        plotLines: [{
          value: 0,
          width: 1,
          color: '#808080'
        }]
      },
      legend: {
        enabled: false
      },
      exporting: {
        enabled: false
      },
      series: [{
        name: 'sent',
        data: []
      }, {
        name: 'consume',
        data: []
      }]
    };

    var eventbus = new EventBus('/eventbus');

    eventbus.onopen = () => {
      eventbus.registerHandler('microservice.monitor.metrics', (err, message) => {
        var res = message.body;
        if (res != null) {
          $scope.metrics = res;
          var seriesArray = $scope.ebChartConfig.series;
          var time = (new Date()).getTime();
          seriesArray[0].data = seriesArray[0].data.concat({
            x: time,
            y: res["vertx.eventbus.messages.sent"].oneSecondRate
          });
          /*seriesArray[1].data = seriesArray[0].data.concat({
           x: time,
           y: res["vertx.eventbus.messages.received"].oneSecondRate
           });*/
          $scope.$apply();
        }
      });
    }

  }]);

monitorApp.controller('AppLogCtrl', ['$scope',
  function ($scope) {
    $scope.logs = [];

    var eventbus = new EventBus('/eventbus');

    eventbus.onopen = () => {
      eventbus.registerHandler('events.log', (err, message) => {
        if (message != null) {
          $scope.logs = $scope.logs.concat(message.body);
          $scope.$apply();
        }
      });
    }
  }]);

monitorApp.controller('AppOrderCtrl', ['$scope',
  function ($scope) {
    $scope.processedAmount = 0;

    $scope.orderEbChartConfig = {
      options: {
        chart: {
          type: 'spline',
          animation: Highcharts.svg
        }
      },
      title: {
        text: "Order processing throughput"
      },
      xAxis: {
        type: 'datetime',
        tickPixelInterval: 150
      },
      yAxis: {
        title: {
          text: 'Value'
        },
        plotLines: [{
          value: 0,
          width: 1,
          color: '#808080'
        }]
      },
      legend: {
        enabled: false
      },
      exporting: {
        enabled: false
      },
      series: [{
        name: 'processed',
        data: []
      }]
    };

    var eventbus = new EventBus('/eventbus');

    eventbus.onopen = () => {
      eventbus.registerHandler('microservice.monitor.metrics', (err, message) => {
        let metrics = message.body;
        if (metrics["vertx.eventbus.handlers.events.service.shopping.to.order"] != null) {
          $scope.processedAmount = metrics["vertx.eventbus.handlers.events.service.shopping.to.order"].count;
          var seriesArray = $scope.ebChartConfig.series;
          var time = (new Date()).getTime();
          seriesArray[0].data = seriesArray[0].data.concat({
            x: time,
            y: processedAmount
          });
          $scope.$apply();
        }
      });
    }

  }]);
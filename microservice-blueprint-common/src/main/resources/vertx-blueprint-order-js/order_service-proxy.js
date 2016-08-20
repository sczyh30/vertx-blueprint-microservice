/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

/** @module vertx-blueprint-order-js/order_service */
!function (factory) {
  if (typeof require === 'function' && typeof module !== 'undefined') {
    factory();
  } else if (typeof define === 'function' && define.amd) {
    // AMD loader
    define('vertx-blueprint-order-js/order_service-proxy', [], factory);
  } else {
    // plain old include
    OrderService = factory();
  }
}(function () {

  /**
   A service interface managing order storage operations.
   <p>
   This service is an event bus service (aka. service proxy).
   </p>

   @class
   */
  var OrderService = function (eb, address) {

    var j_eb = eb;
    var j_address = address;
    var closed = false;
    var that = this;
    var convCharCollection = function (coll) {
      var ret = [];
      for (var i = 0; i < coll.length; i++) {
        ret.push(String.fromCharCode(coll[i]));
      }
      return ret;
    };

    /**
     Initialize the persistence.

     @public
     @param resultHandler {function} async result handler 
     @return {OrderService}
     */
    this.initializePersistence = function (resultHandler) {
      var __args = arguments;
      if (__args.length === 1 && typeof __args[0] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {}, {"action": "initializePersistence"}, function (err, result) {
          __args[0](err, result && result.body);
        });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**
     Retrieve orders belonging to a certain account.

     @public
     @param accountId {string} account id 
     @param resultHandler {function} async result handler 
     @return {OrderService}
     */
    this.retrieveOrdersForAccount = function (accountId, resultHandler) {
      var __args = arguments;
      if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"accountId": __args[0]}, {"action": "retrieveOrdersForAccount"}, function (err, result) {
          __args[1](err, result && result.body);
        });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**
     Save an order into the persistence.

     @public
     @param order {Object} order data object 
     @param resultHandler {function} async result handler 
     @return {OrderService}
     */
    this.createOrder = function (order, resultHandler) {
      var __args = arguments;
      if (__args.length === 2 && (typeof __args[0] === 'object' && __args[0] != null) && typeof __args[1] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"order": __args[0]}, {"action": "createOrder"}, function (err, result) {
          __args[1](err, result && result.body);
        });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**
     Retrieve the order with a certain <code>orderId</code>.

     @public
     @param orderId {number} order id 
     @param resultHandler {function} async result handler 
     @return {OrderService}
     */
    this.retrieveOrder = function (orderId, resultHandler) {
      var __args = arguments;
      if (__args.length === 2 && typeof __args[0] === 'number' && typeof __args[1] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"orderId": __args[0]}, {"action": "retrieveOrder"}, function (err, result) {
          __args[1](err, result && result.body);
        });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

  };

  if (typeof exports !== 'undefined') {
    if (typeof module !== 'undefined' && module.exports) {
      exports = module.exports = OrderService;
    } else {
      exports.OrderService = OrderService;
    }
  } else {
    return OrderService;
  }
});
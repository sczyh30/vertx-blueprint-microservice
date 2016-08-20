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

/** @module vertx-blueprint-payment-js/payment_query_service */
!function (factory) {
  if (typeof require === 'function' && typeof module !== 'undefined') {
    factory();
  } else if (typeof define === 'function' && define.amd) {
    // AMD loader
    define('vertx-blueprint-payment-js/payment_query_service-proxy', [], factory);
  } else {
    // plain old include
    PaymentQueryService = factory();
  }
}(function () {

  /**
   A service interface managing payment transactions query.
   <p>
   This service is an event bus service (aka. service proxy).
   </p>

   @class
   */
  var PaymentQueryService = function (eb, address) {

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
     @param resultHandler {function} the result handler will be called as soon as the initialization has been accomplished. The async result indicates whether the operation was successful or not. 
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
        return;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**
     Add a payment record into the backend persistence.

     @public
     @param payment {Object} payment entity 
     @param resultHandler {function} async result handler 
     */
    this.addPaymentRecord = function (payment, resultHandler) {
      var __args = arguments;
      if (__args.length === 2 && (typeof __args[0] === 'object' && __args[0] != null) && typeof __args[1] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"payment": __args[0]}, {"action": "addPaymentRecord"}, function (err, result) {
          __args[1](err, result && result.body);
        });
        return;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**
     Retrieve payment record from backend by payment id.

     @public
     @param payId {string} payment id 
     @param resultHandler {function} async result handler 
     */
    this.retrievePaymentRecord = function (payId, resultHandler) {
      var __args = arguments;
      if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"payId": __args[0]}, {"action": "retrievePaymentRecord"}, function (err, result) {
          __args[1](err, result && result.body);
        });
        return;
      } else throw new TypeError('function invoked with invalid arguments');
    };

  };

  if (typeof exports !== 'undefined') {
    if (typeof module !== 'undefined' && module.exports) {
      exports = module.exports = PaymentQueryService;
    } else {
      exports.PaymentQueryService = PaymentQueryService;
    }
  } else {
    return PaymentQueryService;
  }
});
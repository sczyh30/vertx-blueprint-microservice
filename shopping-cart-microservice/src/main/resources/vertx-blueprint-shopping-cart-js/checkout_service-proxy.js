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

/** @module vertx-blueprint-shopping-cart-js/checkout_service */
!function (factory) {
  if (typeof require === 'function' && typeof module !== 'undefined') {
    factory();
  } else if (typeof define === 'function' && define.amd) {
    // AMD loader
    define('vertx-blueprint-shopping-cart-js/checkout_service-proxy', [], factory);
  } else {
    // plain old include
    CheckoutService = factory();
  }
}(function () {

  /**
 A service interface for shopping cart checkout logic.
 <p>
 This service is an event bus service (aka. service proxy).
 </p>

 @class
  */
  var CheckoutService = function(eb, address) {

    var j_eb = eb;
    var j_address = address;
    var closed = false;
    var that = this;
    var convCharCollection = function(coll) {
      var ret = [];
      for (var i = 0;i < coll.length;i++) {
        ret.push(String.fromCharCode(coll[i]));
      }
      return ret;
    };

    /**
     Shopping cart checkout.

     @public
     @param userId {string} user id 
     @param handler {function} async result handler 
     */
    this.checkout = function(userId, handler) {
      var __args = arguments;
      if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"userId":__args[0]}, {"action":"checkout"}, function(err, result) { __args[1](err, result &&result.body); });
        return;
      } else throw new TypeError('function invoked with invalid arguments');
    };

  };

  /**
   Create a shopping checkout service instance

   @memberof module:vertx-blueprint-shopping-cart-js/checkout_service
   @param vertx {Vertx} 
   @param discovery {ServiceDiscovery} 
   @return {CheckoutService}
   */
  CheckoutService.createService = function(vertx, discovery) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'object' && __args[0]._jdel && typeof __args[1] === 'object' && __args[1]._jdel) {
      if (closed) {
        throw new Error('Proxy is closed');
      }
      j_eb.send(j_address, {"vertx":__args[0], "discovery":__args[1]}, {"action":"createService"});
      return;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  if (typeof exports !== 'undefined') {
    if (typeof module !== 'undefined' && module.exports) {
      exports = module.exports = CheckoutService;
    } else {
      exports.CheckoutService = CheckoutService;
    }
  } else {
    return CheckoutService;
  }
});
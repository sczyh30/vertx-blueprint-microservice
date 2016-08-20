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

/** @module vertx-blueprint-shopping-cart-js/shopping_cart_service */
var utils = require('vertx-js/util/utils');

var io = Packages.io;
var JsonObject = io.vertx.core.json.JsonObject;
var JShoppingCartService = io.vertx.blueprint.microservice.cart.ShoppingCartService;
var CartEvent = io.vertx.blueprint.microservice.cart.CartEvent;
var ShoppingCart = io.vertx.blueprint.microservice.cart.ShoppingCart;

/**
 A service interface for shopping cart operation.
 <p>
 This service is an event bus service (aka. service proxy).
 </p>

 @class
*/
var ShoppingCartService = function(j_val) {

  var j_shoppingCartService = j_val;
  var that = this;

  /**
   Add cart event to the event source.

   @public
   @param event {Object} cart event 
   @param resultHandler {function} async result handler 
   @return {ShoppingCartService}
   */
  this.addCartEvent = function(event, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && (typeof __args[0] === 'object' && __args[0] != null) && typeof __args[1] === 'function') {
      j_shoppingCartService["addCartEvent(io.vertx.blueprint.microservice.cart.CartEvent,io.vertx.core.Handler)"](event != null ? new CartEvent(new JsonObject(JSON.stringify(event))) : null, function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Get shopping cart of a user.

   @public
   @param userId {string} user id 
   @param resultHandler {function} async result handler 
   @return {ShoppingCartService}
   */
  this.getShoppingCart = function(userId, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_shoppingCartService["getShoppingCart(java.lang.String,io.vertx.core.Handler)"](userId, function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnDataObject(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  // A reference to the underlying Java delegate
  // NOTE! This is an internal API and must not be used in user code.
  // If you rely on this property your code is likely to break if we change it / remove it without warning.
  this._jdel = j_shoppingCartService;
};

// We export the Constructor function
module.exports = ShoppingCartService;
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

/** @module vertx-blueprint-product-js/product_service */
!function (factory) {
  if (typeof require === 'function' && typeof module !== 'undefined') {
    factory();
  } else if (typeof define === 'function' && define.amd) {
    // AMD loader
    define('vertx-blueprint-product-js/product_service-proxy', [], factory);
  } else {
    // plain old include
    ProductService = factory();
  }
}(function () {

  /**
   A service interface managing products.
   <p>
   This service is an event bus service (aka. service proxy)
   </p>

   @class
   */
  var ProductService = function (eb, address) {

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
     @return {ProductService}
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
     Add a product to the persistence.

     @public
     @param product {Object} a product entity that we want to add 
     @param resultHandler {function} the result handler will be called as soon as the product has been added. The async result indicates whether the operation was successful or not. 
     @return {ProductService}
     */
    this.addProduct = function (product, resultHandler) {
      var __args = arguments;
      if (__args.length === 2 && (typeof __args[0] === 'object' && __args[0] != null) && typeof __args[1] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"product": __args[0]}, {"action": "addProduct"}, function (err, result) {
          __args[1](err, result && result.body);
        });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**
     Retrieve the product with certain `productId`.

     @public
     @param productId {string} product id 
     @param resultHandler {function} the result handler will be called as soon as the product has been retrieved. The async result indicates whether the operation was successful or not. 
     @return {ProductService}
     */
    this.retrieveProduct = function (productId, resultHandler) {
      var __args = arguments;
      if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"productId": __args[0]}, {"action": "retrieveProduct"}, function (err, result) {
          __args[1](err, result && result.body);
        });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**
     Retrieve the product price with certain `productId`.

     @public
     @param productId {string} product id 
     @param resultHandler {function} the result handler will be called as soon as the product has been retrieved. The async result indicates whether the operation was successful or not. 
     @return {ProductService}
     */
    this.retrieveProductPrice = function (productId, resultHandler) {
      var __args = arguments;
      if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"productId": __args[0]}, {"action": "retrieveProductPrice"}, function (err, result) {
          __args[1](err, result && result.body);
        });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**
     Retrieve all products.

     @public
     @param resultHandler {function} the result handler will be called as soon as the products have been retrieved. The async result indicates whether the operation was successful or not. 
     @return {ProductService}
     */
    this.retrieveAllProducts = function (resultHandler) {
      var __args = arguments;
      if (__args.length === 1 && typeof __args[0] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {}, {"action": "retrieveAllProducts"}, function (err, result) {
          __args[0](err, result && result.body);
        });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**
     Retrieve products by page.

     @public
     @param page {number} 
     @param resultHandler {function} the result handler will be called as soon as the products have been retrieved. The async result indicates whether the operation was successful or not. 
     @return {ProductService}
     */
    this.retrieveProductsByPage = function (page, resultHandler) {
      var __args = arguments;
      if (__args.length === 2 && typeof __args[0] === 'number' && typeof __args[1] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"page": __args[0]}, {"action": "retrieveProductsByPage"}, function (err, result) {
          __args[1](err, result && result.body);
        });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**
     Delete a product from the persistence

     @public
     @param productId {string} product id 
     @param resultHandler {function} the result handler will be called as soon as the product has been removed. The async result indicates whether the operation was successful or not. 
     @return {ProductService}
     */
    this.deleteProduct = function (productId, resultHandler) {
      var __args = arguments;
      if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"productId": __args[0]}, {"action": "deleteProduct"}, function (err, result) {
          __args[1](err, result && result.body);
        });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**
     Delete all products from the persistence

     @public
     @param resultHandler {function} the result handler will be called as soon as the products have been removed. The async result indicates whether the operation was successful or not. 
     @return {ProductService}
     */
    this.deleteAllProducts = function (resultHandler) {
      var __args = arguments;
      if (__args.length === 1 && typeof __args[0] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {}, {"action": "deleteAllProducts"}, function (err, result) {
          __args[0](err, result && result.body);
        });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

  };

  if (typeof exports !== 'undefined') {
    if (typeof module !== 'undefined' && module.exports) {
      exports = module.exports = ProductService;
    } else {
      exports.ProductService = ProductService;
    }
  } else {
    return ProductService;
  }
});
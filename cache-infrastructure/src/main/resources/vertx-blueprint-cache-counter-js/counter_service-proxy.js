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

/** @module vertx-blueprint-cache-counter-js/counter_service */
!function (factory) {
  if (typeof require === 'function' && typeof module !== 'undefined') {
    factory();
  } else if (typeof define === 'function' && define.amd) {
    // AMD loader
    define('vertx-blueprint-cache-counter-js/counter_service-proxy', [], factory);
  } else {
    // plain old include
    CounterService = factory();
  }
}(function () {

  /**
   A service interface for global cache and counter management using a cache backend (e.g. Redis).
   <p>
   This service is an event bus service (aka. service proxy)
   </p>

   @class
   */
  var CounterService = function (eb, address) {

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
     First add the counter, then retrieve.

     @public
     @param key {string} counter key
     @param resultHandler {function} async result handler
     */
    this.addThenRetrieve = function (key, resultHandler) {
      var __args = arguments;
      if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"key": __args[0]}, {"action": "addThenRetrieve"}, function (err, result) {
          __args[1](err, result && result.body);
        });
        return;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**
     First add the counter by a <code>increment</code>, then retrieve.

     @public
     @param key {string} counter key
     @param increment {number} increment step
     @param resultHandler {function} async result handler
     */
    this.addThenRetrieveBy = function (key, increment, resultHandler) {
      var __args = arguments;
      if (__args.length === 3 && typeof __args[0] === 'string' && typeof __args[1] === 'number' && typeof __args[2] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {
          "key": __args[0],
          "increment": __args[1]
        }, {"action": "addThenRetrieveBy"}, function (err, result) {
          __args[2](err, result && result.body);
        });
        return;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**
     First retrieve the counter, then add.

     @public
     @param key {string} counter key
     @param resultHandler {function} async result handler
     */
    this.retrieveThenAdd = function (key, resultHandler) {
      var __args = arguments;
      if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"key": __args[0]}, {"action": "retrieveThenAdd"}, function (err, result) {
          __args[1](err, result && result.body);
        });
        return;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**
     Reset the value of the counter with a certain <code>key</code>

     @public
     @param key {string} counter key
     @param resultHandler {function} async result handler
     */
    this.reset = function (key, resultHandler) {
      var __args = arguments;
      if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"key": __args[0]}, {"action": "reset"}, function (err, result) {
          __args[1](err, result && result.body);
        });
        return;
      } else throw new TypeError('function invoked with invalid arguments');
    };

  };

  if (typeof exports !== 'undefined') {
    if (typeof module !== 'undefined' && module.exports) {
      exports = module.exports = CounterService;
    } else {
      exports.CounterService = CounterService;
    }
  } else {
    return CounterService;
  }
});
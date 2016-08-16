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

/** @module vertx-blueprint-store-js/store_crud_service */
var utils = require('vertx-js/util/utils');

var io = Packages.io;
var JsonObject = io.vertx.core.json.JsonObject;
var JStoreCRUDService = io.vertx.blueprint.microservice.store.StoreCRUDService;
var Store = io.vertx.blueprint.microservice.store.Store;

/**
 A service interface for online store CURD operation.
 <p>
 This service is an event bus service (aka. service proxy).
 </p>

 @class
 */
var StoreCRUDService = function (j_val) {

  var j_storeCRUDService = j_val;
  var that = this;

  /**
   Save an online store to the persistence layer. This is a so called `upsert` operation.
   This is used to update store info, or just apply for a new store.

   @public
   @param store {Object} store object 
   @param resultHandler {function} async result handler 
   */
  this.saveStore = function (store, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && (typeof __args[0] === 'object' && __args[0] != null) && typeof __args[1] === 'function') {
      j_storeCRUDService["saveStore(io.vertx.blueprint.microservice.store.Store,io.vertx.core.Handler)"](store != null ? new Store(new JsonObject(JSON.stringify(store))) : null, function (ar) {
        if (ar.succeeded()) {
          resultHandler(null, null);
        } else {
          resultHandler(null, ar.cause());
        }
      });
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Retrieve an online store by seller id.

   @public
   @param sellerId {string} seller id, refers to an independent online store 
   @param resultHandler {function} async result handler 
   */
  this.retrieveStore = function (sellerId, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_storeCRUDService["retrieveStore(java.lang.String,io.vertx.core.Handler)"](sellerId, function (ar) {
        if (ar.succeeded()) {
          resultHandler(utils.convReturnDataObject(ar.result()), null);
        } else {
          resultHandler(null, ar.cause());
        }
      });
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Remove an online store whose seller is <code>sellerId</code>.
   This is used to close an online store.

   @public
   @param sellerId {string} seller id, refers to an independent online store 
   @param resultHandler {function} async result handler 
   */
  this.removeStore = function (sellerId, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_storeCRUDService["removeStore(java.lang.String,io.vertx.core.Handler)"](sellerId, function (ar) {
        if (ar.succeeded()) {
          resultHandler(null, null);
        } else {
          resultHandler(null, ar.cause());
        }
      });
    } else throw new TypeError('function invoked with invalid arguments');
  };

  // A reference to the underlying Java delegate
  // NOTE! This is an internal API and must not be used in user code.
  // If you rely on this property your code is likely to break if we change it / remove it without warning.
  this._jdel = j_storeCRUDService;
};

// We export the Constructor function
module.exports = StoreCRUDService;
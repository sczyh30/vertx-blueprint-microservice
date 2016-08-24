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

/** @module vertx-blueprint-user-account-js/account_service */
var utils = require('vertx-js/util/utils');

var io = Packages.io;
var JsonObject = io.vertx.core.json.JsonObject;
var JAccountService = io.vertx.blueprint.microservice.account.AccountService;
var Account = io.vertx.blueprint.microservice.account.Account;

/**
 A service interface managing user accounts.
 <p>
 This service is an event bus service (aka. service proxy).
 </p>

 @class
 */
var AccountService = function (j_val) {

  var j_accountService = j_val;
  var that = this;

  /**
   Initialize the persistence.

   @public
   @param resultHandler {function} the result handler will be called as soon as the initialization has been accomplished. The async result indicates whether the operation was successful or not. 
   @return {AccountService}
   */
  this.initializePersistence = function (resultHandler) {
    var __args = arguments;
    if (__args.length === 1 && typeof __args[0] === 'function') {
      j_accountService["initializePersistence(io.vertx.core.Handler)"](function (ar) {
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
   Add a account to the persistence.

   @public
   @param account {Object} a account entity that we want to add 
   @param resultHandler {function} the result handler will be called as soon as the account has been added. The async result indicates whether the operation was successful or not. 
   @return {AccountService}
   */
  this.addAccount = function (account, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && (typeof __args[0] === 'object' && __args[0] != null) && typeof __args[1] === 'function') {
      j_accountService["addAccount(io.vertx.blueprint.microservice.account.Account,io.vertx.core.Handler)"](account != null ? new Account(new JsonObject(JSON.stringify(account))) : null, function (ar) {
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
   Retrieve the user account with certain `id`.

   @public
   @param id {string} user account id 
   @param resultHandler {function} the result handler will be called as soon as the user has been retrieved. The async result indicates whether the operation was successful or not. 
   @return {AccountService}
   */
  this.retrieveAccount = function (id, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_accountService["retrieveAccount(java.lang.String,io.vertx.core.Handler)"](id, function (ar) {
        if (ar.succeeded()) {
          resultHandler(utils.convReturnDataObject(ar.result()), null);
        } else {
          resultHandler(null, ar.cause());
        }
      });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Retrieve the user account with certain `username`.

   @public
   @param username {string} username 
   @param resultHandler {function} the result handler will be called as soon as the user has been retrieved. The async result indicates whether the operation was successful or not. 
   @return {AccountService}
   */
  this.retrieveByUsername = function (username, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_accountService["retrieveByUsername(java.lang.String,io.vertx.core.Handler)"](username, function (ar) {
        if (ar.succeeded()) {
          resultHandler(utils.convReturnDataObject(ar.result()), null);
        } else {
          resultHandler(null, ar.cause());
        }
      });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Retrieve all user accounts.

   @public
   @param resultHandler {function} the result handler will be called as soon as the users have been retrieved. The async result indicates whether the operation was successful or not. 
   @return {AccountService}
   */
  this.retrieveAllAccounts = function (resultHandler) {
    var __args = arguments;
    if (__args.length === 1 && typeof __args[0] === 'function') {
      j_accountService["retrieveAllAccounts(io.vertx.core.Handler)"](function (ar) {
        if (ar.succeeded()) {
          resultHandler(utils.convReturnListSetDataObject(ar.result()), null);
        } else {
          resultHandler(null, ar.cause());
        }
      });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Update user account info.

   @public
   @param account {Object} a account entity that we want to update 
   @param resultHandler {function} the result handler will be called as soon as the account has been added. The async result indicates whether the operation was successful or not. 
   @return {AccountService}
   */
  this.updateAccount = function (account, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && (typeof __args[0] === 'object' && __args[0] != null) && typeof __args[1] === 'function') {
      j_accountService["updateAccount(io.vertx.blueprint.microservice.account.Account,io.vertx.core.Handler)"](account != null ? new Account(new JsonObject(JSON.stringify(account))) : null, function (ar) {
        if (ar.succeeded()) {
          resultHandler(utils.convReturnDataObject(ar.result()), null);
        } else {
          resultHandler(null, ar.cause());
        }
      });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**
   Delete a user account from the persistence

   @public
   @param id {string} user account id 
   @param resultHandler {function} the result handler will be called as soon as the user has been removed. The async result indicates whether the operation was successful or not. 
   @return {AccountService}
   */
  this.deleteAccount = function (id, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_accountService["deleteAccount(java.lang.String,io.vertx.core.Handler)"](id, function (ar) {
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
   Delete all user accounts from the persistence

   @public
   @param resultHandler {function} the result handler will be called as soon as the users have been removed. The async result indicates whether the operation was successful or not. 
   @return {AccountService}
   */
  this.deleteAllAccounts = function (resultHandler) {
    var __args = arguments;
    if (__args.length === 1 && typeof __args[0] === 'function') {
      j_accountService["deleteAllAccounts(io.vertx.core.Handler)"](function (ar) {
        if (ar.succeeded()) {
          resultHandler(null, null);
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
  this._jdel = j_accountService;
};

// We export the Constructor function
module.exports = AccountService;
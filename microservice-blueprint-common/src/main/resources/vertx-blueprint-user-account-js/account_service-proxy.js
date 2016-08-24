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
!function (factory) {
  if (typeof require === 'function' && typeof module !== 'undefined') {
    factory();
  } else if (typeof define === 'function' && define.amd) {
    // AMD loader
    define('vertx-blueprint-user-account-js/account_service-proxy', [], factory);
  } else {
    // plain old include
    AccountService = factory();
  }
}(function () {

  /**
   A service interface managing user accounts.
   <p>
   This service is an event bus service (aka. service proxy).
   </p>

   @class
   */
  var AccountService = function (eb, address) {

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
     @return {AccountService}
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
     Add a account to the persistence.

     @public
     @param account {Object} a account entity that we want to add 
     @param resultHandler {function} the result handler will be called as soon as the account has been added. The async result indicates whether the operation was successful or not. 
     @return {AccountService}
     */
    this.addAccount = function (account, resultHandler) {
      var __args = arguments;
      if (__args.length === 2 && (typeof __args[0] === 'object' && __args[0] != null) && typeof __args[1] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"account": __args[0]}, {"action": "addAccount"}, function (err, result) {
          __args[1](err, result && result.body);
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
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"id": __args[0]}, {"action": "retrieveAccount"}, function (err, result) {
          __args[1](err, result && result.body);
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
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"username": __args[0]}, {"action": "retrieveByUsername"}, function (err, result) {
          __args[1](err, result && result.body);
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
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {}, {"action": "retrieveAllAccounts"}, function (err, result) {
          __args[0](err, result && result.body);
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
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"account": __args[0]}, {"action": "updateAccount"}, function (err, result) {
          __args[1](err, result && result.body);
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
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"id": __args[0]}, {"action": "deleteAccount"}, function (err, result) {
          __args[1](err, result && result.body);
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
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {}, {"action": "deleteAllAccounts"}, function (err, result) {
          __args[0](err, result && result.body);
        });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

  };

  if (typeof exports !== 'undefined') {
    if (typeof module !== 'undefined' && module.exports) {
      exports = module.exports = AccountService;
    } else {
      exports.AccountService = AccountService;
    }
  } else {
    return AccountService;
  }
});
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

package io.vertx.blueprint.microservice.account.rxjava;

import java.util.Map;
import rx.Observable;
import java.util.List;
import io.vertx.core.AsyncResult;
import io.vertx.blueprint.microservice.account.Account;
import io.vertx.core.Handler;

/**
 * A service interface managing user accounts.
 * <p>
 * This service is an event bus service (aka. service proxy).
 * </p>
 *
 * <p/>
 * NOTE: This class has been automatically generated from the {@link io.vertx.blueprint.microservice.account.AccountService original} non RX-ified interface using Vert.x codegen.
 */

public class AccountService {

  final io.vertx.blueprint.microservice.account.AccountService delegate;

  public AccountService(io.vertx.blueprint.microservice.account.AccountService delegate) {
    this.delegate = delegate;
  }

  public Object getDelegate() {
    return delegate;
  }

  /**
   * Initialize the persistence.
   * @param resultHandler the result handler will be called as soon as the initialization has been accomplished. The async result indicates whether the operation was successful or not.
   * @return 
   */
  public AccountService initializePersistence(Handler<AsyncResult<Void>> resultHandler) { 
    delegate.initializePersistence(resultHandler);
    return this;
  }

  /**
   * Initialize the persistence.
   * @return 
   */
  public Observable<Void> initializePersistenceObservable() { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    initializePersistence(resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Add a account to the persistence.
   * @param account a account entity that we want to add
   * @param resultHandler the result handler will be called as soon as the account has been added. The async result indicates whether the operation was successful or not.
   * @return 
   */
  public AccountService addAccount(Account account, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.addAccount(account, resultHandler);
    return this;
  }

  /**
   * Add a account to the persistence.
   * @param account a account entity that we want to add
   * @return 
   */
  public Observable<Void> addAccountObservable(Account account) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    addAccount(account, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Retrieve the user account with certain `id`.
   * @param id user account id
   * @param resultHandler the result handler will be called as soon as the user has been retrieved. The async result indicates whether the operation was successful or not.
   * @return 
   */
  public AccountService retrieveAccount(String id, Handler<AsyncResult<Account>> resultHandler) { 
    delegate.retrieveAccount(id, resultHandler);
    return this;
  }

  /**
   * Retrieve the user account with certain `id`.
   * @param id user account id
   * @return 
   */
  public Observable<Account> retrieveAccountObservable(String id) { 
    io.vertx.rx.java.ObservableFuture<Account> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    retrieveAccount(id, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Retrieve the user account with certain `username`.
   * @param username username
   * @param resultHandler the result handler will be called as soon as the user has been retrieved. The async result indicates whether the operation was successful or not.
   * @return 
   */
  public AccountService retrieveByUsername(String username, Handler<AsyncResult<Account>> resultHandler) { 
    delegate.retrieveByUsername(username, resultHandler);
    return this;
  }

  /**
   * Retrieve the user account with certain `username`.
   * @param username username
   * @return 
   */
  public Observable<Account> retrieveByUsernameObservable(String username) { 
    io.vertx.rx.java.ObservableFuture<Account> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    retrieveByUsername(username, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Retrieve all user accounts.
   * @param resultHandler the result handler will be called as soon as the users have been retrieved. The async result indicates whether the operation was successful or not.
   * @return 
   */
  public AccountService retrieveAllAccounts(Handler<AsyncResult<List<Account>>> resultHandler) { 
    delegate.retrieveAllAccounts(resultHandler);
    return this;
  }

  /**
   * Retrieve all user accounts.
   * @return 
   */
  public Observable<List<Account>> retrieveAllAccountsObservable() { 
    io.vertx.rx.java.ObservableFuture<List<Account>> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    retrieveAllAccounts(resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Update user account info.
   * @param account a account entity that we want to update
   * @param resultHandler the result handler will be called as soon as the account has been added. The async result indicates whether the operation was successful or not.
   * @return 
   */
  public AccountService updateAccount(Account account, Handler<AsyncResult<Account>> resultHandler) { 
    delegate.updateAccount(account, resultHandler);
    return this;
  }

  /**
   * Update user account info.
   * @param account a account entity that we want to update
   * @return 
   */
  public Observable<Account> updateAccountObservable(Account account) { 
    io.vertx.rx.java.ObservableFuture<Account> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    updateAccount(account, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Delete a user account from the persistence
   * @param id user account id
   * @param resultHandler the result handler will be called as soon as the user has been removed. The async result indicates whether the operation was successful or not.
   * @return 
   */
  public AccountService deleteAccount(String id, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.deleteAccount(id, resultHandler);
    return this;
  }

  /**
   * Delete a user account from the persistence
   * @param id user account id
   * @return 
   */
  public Observable<Void> deleteAccountObservable(String id) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    deleteAccount(id, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Delete all user accounts from the persistence
   * @param resultHandler the result handler will be called as soon as the users have been removed. The async result indicates whether the operation was successful or not.
   * @return 
   */
  public AccountService deleteAllAccounts(Handler<AsyncResult<Void>> resultHandler) { 
    delegate.deleteAllAccounts(resultHandler);
    return this;
  }

  /**
   * Delete all user accounts from the persistence
   * @return 
   */
  public Observable<Void> deleteAllAccountsObservable() { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    deleteAllAccounts(resultHandler.toHandler());
    return resultHandler;
  }


  public static AccountService newInstance(io.vertx.blueprint.microservice.account.AccountService arg) {
    return arg != null ? new AccountService(arg) : null;
  }
}

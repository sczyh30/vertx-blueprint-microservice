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

package io.vertx.blueprint.microservice.store.rxjava;

import java.util.Map;
import rx.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.blueprint.microservice.store.Store;

/**
 * A service interface for online store CURD operation.
 * <p>
 * This service is an event bus service (aka. service proxy).
 * </p>
 *
 * <p/>
 * NOTE: This class has been automatically generated from the {@link io.vertx.blueprint.microservice.store.StoreCRUDService original} non RX-ified interface using Vert.x codegen.
 */

public class StoreCRUDService {

  final io.vertx.blueprint.microservice.store.StoreCRUDService delegate;

  public StoreCRUDService(io.vertx.blueprint.microservice.store.StoreCRUDService delegate) {
    this.delegate = delegate;
  }

  public Object getDelegate() {
    return delegate;
  }

  public void saveStore(Store store, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.saveStore(store, resultHandler);
  }

  public Observable<Void> saveStoreObservable(Store store) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    saveStore(store, resultHandler.toHandler());
    return resultHandler;
  }

  public void retrieveStore(String sellerId, Handler<AsyncResult<Store>> resultHandler) { 
    delegate.retrieveStore(sellerId, resultHandler);
  }

  public Observable<Store> retrieveStoreObservable(String sellerId) { 
    io.vertx.rx.java.ObservableFuture<Store> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    retrieveStore(sellerId, resultHandler.toHandler());
    return resultHandler;
  }

  public void removeStore(String sellerId, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.removeStore(sellerId, resultHandler);
  }

  public Observable<Void> removeStoreObservable(String sellerId) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    removeStore(sellerId, resultHandler.toHandler());
    return resultHandler;
  }


  public static StoreCRUDService newInstance(io.vertx.blueprint.microservice.store.StoreCRUDService arg) {
    return arg != null ? new StoreCRUDService(arg) : null;
  }
}

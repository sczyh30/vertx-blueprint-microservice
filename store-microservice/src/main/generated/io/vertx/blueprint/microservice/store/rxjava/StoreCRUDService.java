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
import rx.Single;
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

@io.vertx.lang.rxjava.RxGen(io.vertx.blueprint.microservice.store.StoreCRUDService.class)
public class StoreCRUDService {

  public static final io.vertx.lang.rxjava.TypeArg<StoreCRUDService> __TYPE_ARG = new io.vertx.lang.rxjava.TypeArg<>(
    obj -> new StoreCRUDService((io.vertx.blueprint.microservice.store.StoreCRUDService) obj),
    StoreCRUDService::getDelegate
  );

  private final io.vertx.blueprint.microservice.store.StoreCRUDService delegate;
  
  public StoreCRUDService(io.vertx.blueprint.microservice.store.StoreCRUDService delegate) {
    this.delegate = delegate;
  }

  public io.vertx.blueprint.microservice.store.StoreCRUDService getDelegate() {
    return delegate;
  }

  /**
   * Save an online store to the persistence layer. This is a so called `upsert` operation.
   * This is used to update store info, or just apply for a new store.
   * @param store store object
   * @param resultHandler async result handler
   */
  public void saveStore(Store store, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.saveStore(store, resultHandler);
  }

  /**
   * Save an online store to the persistence layer. This is a so called `upsert` operation.
   * This is used to update store info, or just apply for a new store.
   * @param store store object
   * @return 
   */
  public Single<Void> rxSaveStore(Store store) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      saveStore(store, fut);
    }));
  }

  /**
   * Retrieve an online store by seller id.
   * @param sellerId seller id, refers to an independent online store
   * @param resultHandler async result handler
   */
  public void retrieveStore(String sellerId, Handler<AsyncResult<Store>> resultHandler) { 
    delegate.retrieveStore(sellerId, resultHandler);
  }

  /**
   * Retrieve an online store by seller id.
   * @param sellerId seller id, refers to an independent online store
   * @return 
   */
  public Single<Store> rxRetrieveStore(String sellerId) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      retrieveStore(sellerId, fut);
    }));
  }

  /**
   * Remove an online store whose seller is <code>sellerId</code>.
   * This is used to close an online store.
   * @param sellerId seller id, refers to an independent online store
   * @param resultHandler async result handler
   */
  public void removeStore(String sellerId, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.removeStore(sellerId, resultHandler);
  }

  /**
   * Remove an online store whose seller is <code>sellerId</code>.
   * This is used to close an online store.
   * @param sellerId seller id, refers to an independent online store
   * @return 
   */
  public Single<Void> rxRemoveStore(String sellerId) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      removeStore(sellerId, fut);
    }));
  }


  public static StoreCRUDService newInstance(io.vertx.blueprint.microservice.store.StoreCRUDService arg) {
    return arg != null ? new StoreCRUDService(arg) : null;
  }
}

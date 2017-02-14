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

package io.vertx.blueprint.microservice.cache.rxjava;

import java.util.Map;
import rx.Observable;
import rx.Single;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * A service interface for global cache and counter management using a cache backend (e.g. Redis).
 * <p>
 * This service is an event bus service (aka. service proxy)
 * </p>
 *
 * <p/>
 * NOTE: This class has been automatically generated from the {@link io.vertx.blueprint.microservice.cache.CounterService original} non RX-ified interface using Vert.x codegen.
 */

@io.vertx.lang.rxjava.RxGen(io.vertx.blueprint.microservice.cache.CounterService.class)
public class CounterService {

  public static final io.vertx.lang.rxjava.TypeArg<CounterService> __TYPE_ARG = new io.vertx.lang.rxjava.TypeArg<>(
    obj -> new CounterService((io.vertx.blueprint.microservice.cache.CounterService) obj),
    CounterService::getDelegate
  );

  private final io.vertx.blueprint.microservice.cache.CounterService delegate;
  
  public CounterService(io.vertx.blueprint.microservice.cache.CounterService delegate) {
    this.delegate = delegate;
  }

  public io.vertx.blueprint.microservice.cache.CounterService getDelegate() {
    return delegate;
  }

  /**
   * First add the counter, then retrieve.
   * @param key counter key
   * @param resultHandler async result handler
   */
  public void addThenRetrieve(String key, Handler<AsyncResult<Long>> resultHandler) { 
    delegate.addThenRetrieve(key, resultHandler);
  }

  /**
   * First add the counter, then retrieve.
   * @param key counter key
   * @return 
   */
  public Single<Long> rxAddThenRetrieve(String key) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      addThenRetrieve(key, fut);
    }));
  }

  /**
   * First add the counter by a <code>increment</code>, then retrieve.
   * @param key counter key
   * @param increment increment step
   * @param resultHandler async result handler
   */
  public void addThenRetrieveBy(String key, Long increment, Handler<AsyncResult<Long>> resultHandler) { 
    delegate.addThenRetrieveBy(key, increment, resultHandler);
  }

  /**
   * First add the counter by a <code>increment</code>, then retrieve.
   * @param key counter key
   * @param increment increment step
   * @return 
   */
  public Single<Long> rxAddThenRetrieveBy(String key, Long increment) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      addThenRetrieveBy(key, increment, fut);
    }));
  }

  /**
   * First retrieve the counter, then add.
   * @param key counter key
   * @param resultHandler async result handler
   */
  public void retrieveThenAdd(String key, Handler<AsyncResult<Long>> resultHandler) { 
    delegate.retrieveThenAdd(key, resultHandler);
  }

  /**
   * First retrieve the counter, then add.
   * @param key counter key
   * @return 
   */
  public Single<Long> rxRetrieveThenAdd(String key) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      retrieveThenAdd(key, fut);
    }));
  }

  /**
   * Reset the value of the counter with a certain <code>key</code>
   * @param key counter key
   * @param resultHandler async result handler
   */
  public void reset(String key, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.reset(key, resultHandler);
  }

  /**
   * Reset the value of the counter with a certain <code>key</code>
   * @param key counter key
   * @return 
   */
  public Single<Void> rxReset(String key) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      reset(key, fut);
    }));
  }


  public static CounterService newInstance(io.vertx.blueprint.microservice.cache.CounterService arg) {
    return arg != null ? new CounterService(arg) : null;
  }
}

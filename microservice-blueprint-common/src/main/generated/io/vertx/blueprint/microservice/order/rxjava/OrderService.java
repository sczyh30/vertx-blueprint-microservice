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

package io.vertx.blueprint.microservice.order.rxjava;

import java.util.Map;
import rx.Observable;
import java.util.List;
import io.vertx.blueprint.microservice.order.Order;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * A service interface managing order storage operations.
 * <p>
 * This service is an event bus service (aka. service proxy).
 * </p>
 *
 * <p/>
 * NOTE: This class has been automatically generated from the {@link io.vertx.blueprint.microservice.order.OrderService original} non RX-ified interface using Vert.x codegen.
 */

public class OrderService {

  final io.vertx.blueprint.microservice.order.OrderService delegate;

  public OrderService(io.vertx.blueprint.microservice.order.OrderService delegate) {
    this.delegate = delegate;
  }

  public Object getDelegate() {
    return delegate;
  }

  /**
   * Initialize the persistence.
   * @param resultHandler async result handler
   * @return 
   */
  public OrderService initializePersistence(Handler<AsyncResult<Void>> resultHandler) { 
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
   * Retrieve orders belonging to a certain account.
   * @param accountId account id
   * @param resultHandler async result handler
   * @return 
   */
  public OrderService retrieveOrdersForAccount(String accountId, Handler<AsyncResult<List<Order>>> resultHandler) { 
    delegate.retrieveOrdersForAccount(accountId, resultHandler);
    return this;
  }

  /**
   * Retrieve orders belonging to a certain account.
   * @param accountId account id
   * @return 
   */
  public Observable<List<Order>> retrieveOrdersForAccountObservable(String accountId) { 
    io.vertx.rx.java.ObservableFuture<List<Order>> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    retrieveOrdersForAccount(accountId, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Save an order into the persistence.
   * @param order order data object
   * @param resultHandler async result handler
   * @return 
   */
  public OrderService createOrder(Order order, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.createOrder(order, resultHandler);
    return this;
  }

  /**
   * Save an order into the persistence.
   * @param order order data object
   * @return 
   */
  public Observable<Void> createOrderObservable(Order order) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    createOrder(order, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Retrieve the order with a certain <code>orderId</code>.
   * @param orderId order id
   * @param resultHandler async result handler
   * @return 
   */
  public OrderService retrieveOrder(Long orderId, Handler<AsyncResult<Order>> resultHandler) { 
    delegate.retrieveOrder(orderId, resultHandler);
    return this;
  }

  /**
   * Retrieve the order with a certain <code>orderId</code>.
   * @param orderId order id
   * @return 
   */
  public Observable<Order> retrieveOrderObservable(Long orderId) { 
    io.vertx.rx.java.ObservableFuture<Order> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    retrieveOrder(orderId, resultHandler.toHandler());
    return resultHandler;
  }


  public static OrderService newInstance(io.vertx.blueprint.microservice.order.OrderService arg) {
    return arg != null ? new OrderService(arg) : null;
  }
}

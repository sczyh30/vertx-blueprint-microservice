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

package io.vertx.blueprint.microservice.payment.rxjava;

import java.util.Map;
import rx.Observable;
import io.vertx.blueprint.microservice.payment.Payment;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * A service interface managing payment transactions query.
 * <p>
 * This service is an event bus service (aka. service proxy).
 * </p>
 *
 * <p/>
 * NOTE: This class has been automatically generated from the {@link io.vertx.blueprint.microservice.payment.PaymentQueryService original} non RX-ified interface using Vert.x codegen.
 */

public class PaymentQueryService {

  final io.vertx.blueprint.microservice.payment.PaymentQueryService delegate;

  public PaymentQueryService(io.vertx.blueprint.microservice.payment.PaymentQueryService delegate) {
    this.delegate = delegate;
  }

  public Object getDelegate() {
    return delegate;
  }

  /**
   * Initialize the persistence.
   * @param resultHandler the result handler will be called as soon as the initialization has been accomplished. The async result indicates whether the operation was successful or not.
   */
  public void initializePersistence(Handler<AsyncResult<Void>> resultHandler) { 
    delegate.initializePersistence(resultHandler);
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
   * Add a payment record into the backend persistence.
   * @param payment payment entity
   * @param resultHandler async result handler
   */
  public void addPaymentRecord(Payment payment, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.addPaymentRecord(payment, resultHandler);
  }

  /**
   * Add a payment record into the backend persistence.
   * @param payment payment entity
   * @return 
   */
  public Observable<Void> addPaymentRecordObservable(Payment payment) { 
    io.vertx.rx.java.ObservableFuture<Void> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    addPaymentRecord(payment, resultHandler.toHandler());
    return resultHandler;
  }

  /**
   * Retrieve payment record from backend by payment id.
   * @param payId payment id
   * @param resultHandler async result handler
   */
  public void retrievePaymentRecord(String payId, Handler<AsyncResult<Payment>> resultHandler) { 
    delegate.retrievePaymentRecord(payId, resultHandler);
  }

  /**
   * Retrieve payment record from backend by payment id.
   * @param payId payment id
   * @return 
   */
  public Observable<Payment> retrievePaymentRecordObservable(String payId) { 
    io.vertx.rx.java.ObservableFuture<Payment> resultHandler = io.vertx.rx.java.RxHelper.observableFuture();
    retrievePaymentRecord(payId, resultHandler.toHandler());
    return resultHandler;
  }


  public static PaymentQueryService newInstance(io.vertx.blueprint.microservice.payment.PaymentQueryService arg) {
    return arg != null ? new PaymentQueryService(arg) : null;
  }
}

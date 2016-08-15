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

package io.vertx.blueprint.microservice.cart.rxjava;

import java.util.Map;
import rx.Observable;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.servicediscovery.ServiceDiscovery;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.blueprint.microservice.cart.CheckoutResult;

/**
 * A service interface for shopping cart checkout logic.
 * <p>
 * This service is an event bus service (aka. service proxy).
 * </p>
 *
 * <p/>
 * NOTE: This class has been automatically generated from the {@link io.vertx.blueprint.microservice.cart.CheckoutService original} non RX-ified interface using Vert.x codegen.
 */

public class CheckoutService {

  final io.vertx.blueprint.microservice.cart.CheckoutService delegate;

  public CheckoutService(io.vertx.blueprint.microservice.cart.CheckoutService delegate) {
    this.delegate = delegate;
  }

  public Object getDelegate() {
    return delegate;
  }

  /**
   * Create a shopping checkout service instance
   * @param vertx 
   * @param discovery 
   * @return 
   */
  public static CheckoutService createService(Vertx vertx, ServiceDiscovery discovery) { 
    CheckoutService ret = CheckoutService.newInstance(io.vertx.blueprint.microservice.cart.CheckoutService.createService((io.vertx.core.Vertx)vertx.getDelegate(), (io.vertx.servicediscovery.ServiceDiscovery)discovery.getDelegate()));
    return ret;
  }

  /**
   * Shopping cart checkout.
   * @param userId user id
   * @param handler async result handler
   */
  public void checkout(String userId, Handler<AsyncResult<CheckoutResult>> handler) { 
    delegate.checkout(userId, handler);
  }

  /**
   * Shopping cart checkout.
   * @param userId user id
   * @return 
   */
  public Observable<CheckoutResult> checkoutObservable(String userId) { 
    io.vertx.rx.java.ObservableFuture<CheckoutResult> handler = io.vertx.rx.java.RxHelper.observableFuture();
    checkout(userId, handler.toHandler());
    return handler;
  }


  public static CheckoutService newInstance(io.vertx.blueprint.microservice.cart.CheckoutService arg) {
    return arg != null ? new CheckoutService(arg) : null;
  }
}

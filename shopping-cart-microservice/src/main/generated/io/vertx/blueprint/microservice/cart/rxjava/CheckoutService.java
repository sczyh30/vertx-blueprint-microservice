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
import rx.Single;
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

@io.vertx.lang.rxjava.RxGen(io.vertx.blueprint.microservice.cart.CheckoutService.class)
public class CheckoutService {

  public static final io.vertx.lang.rxjava.TypeArg<CheckoutService> __TYPE_ARG = new io.vertx.lang.rxjava.TypeArg<>(
    obj -> new CheckoutService((io.vertx.blueprint.microservice.cart.CheckoutService) obj),
    CheckoutService::getDelegate
  );

  private final io.vertx.blueprint.microservice.cart.CheckoutService delegate;
  
  public CheckoutService(io.vertx.blueprint.microservice.cart.CheckoutService delegate) {
    this.delegate = delegate;
  }

  public io.vertx.blueprint.microservice.cart.CheckoutService getDelegate() {
    return delegate;
  }

  /**
   * Create a shopping checkout service instance
   * @param vertx 
   * @param discovery 
   * @return 
   */
  public static CheckoutService createService(Vertx vertx, ServiceDiscovery discovery) { 
    CheckoutService ret = CheckoutService.newInstance(io.vertx.blueprint.microservice.cart.CheckoutService.createService(vertx.getDelegate(), discovery.getDelegate()));
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
  public Single<CheckoutResult> rxCheckout(String userId) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      checkout(userId, fut);
    }));
  }


  public static CheckoutService newInstance(io.vertx.blueprint.microservice.cart.CheckoutService arg) {
    return arg != null ? new CheckoutService(arg) : null;
  }
}

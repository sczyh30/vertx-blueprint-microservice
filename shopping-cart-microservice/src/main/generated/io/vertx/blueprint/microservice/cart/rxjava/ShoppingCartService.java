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
import io.vertx.blueprint.microservice.cart.CartEvent;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.blueprint.microservice.cart.ShoppingCart;

/**
 * A service interface for shopping cart operation.
 * <p>
 * This service is an event bus service (aka. service proxy).
 * </p>
 *
 * <p/>
 * NOTE: This class has been automatically generated from the {@link io.vertx.blueprint.microservice.cart.ShoppingCartService original} non RX-ified interface using Vert.x codegen.
 */

@io.vertx.lang.rxjava.RxGen(io.vertx.blueprint.microservice.cart.ShoppingCartService.class)
public interface ShoppingCartService {

  io.vertx.blueprint.microservice.cart.ShoppingCartService getDelegate();

  /**
   * Add cart event to the event source.
   * @param event cart event
   * @param resultHandler async result handler
   * @return 
   */
  public ShoppingCartService addCartEvent(CartEvent event, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Get shopping cart of a user.
   * @param userId user id
   * @param resultHandler async result handler
   * @return 
   */
  public ShoppingCartService getShoppingCart(String userId, Handler<AsyncResult<ShoppingCart>> resultHandler);


  public static ShoppingCartService newInstance(io.vertx.blueprint.microservice.cart.ShoppingCartService arg) {
    return arg != null ? new ShoppingCartServiceImpl(arg) : null;
  }
}

class ShoppingCartServiceImpl implements ShoppingCartService {
  private final io.vertx.blueprint.microservice.cart.ShoppingCartService delegate;
  
  public ShoppingCartServiceImpl(io.vertx.blueprint.microservice.cart.ShoppingCartService delegate) {
    this.delegate = delegate;
  }

  public io.vertx.blueprint.microservice.cart.ShoppingCartService getDelegate() {
    return delegate;
  }

  /**
   * Add cart event to the event source.
   * @param event cart event
   * @param resultHandler async result handler
   * @return 
   */
  public ShoppingCartService addCartEvent(CartEvent event, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.addCartEvent(event, resultHandler);
    return this;
  }

  /**
   * Add cart event to the event source.
   * @param event cart event
   * @return 
   */
  public Single<Void> rxAddCartEvent(CartEvent event) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      addCartEvent(event, fut);
    }));
  }

  /**
   * Get shopping cart of a user.
   * @param userId user id
   * @param resultHandler async result handler
   * @return 
   */
  public ShoppingCartService getShoppingCart(String userId, Handler<AsyncResult<ShoppingCart>> resultHandler) { 
    delegate.getShoppingCart(userId, resultHandler);
    return this;
  }

  /**
   * Get shopping cart of a user.
   * @param userId user id
   * @return 
   */
  public Single<ShoppingCart> rxGetShoppingCart(String userId) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      getShoppingCart(userId, fut);
    }));
  }

}

package io.vertx.blueprint.microservice.shopping.impl;

import io.vertx.blueprint.microservice.shopping.ShopService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 * A simple implementation for {@link io.vertx.blueprint.microservice.shopping.ShopService}
 */
public class ShopServiceImpl implements ShopService {

  @Override
  public ShopService buy(String userId, String productId, int amount, Handler<AsyncResult<JsonObject>> handler) {
    // TODO
    return this;
  }
}

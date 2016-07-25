package io.vertx.blueprint.microservice.shopping;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 * A service interface for simple shopping logic
 * <p>
 * This service is an event bus service (aka. service proxy)
 * </p>
 *
 * @author Eric Zhao
 */
@VertxGen
@ProxyGen
public interface ShopService {

  /**
   * Buy some things :-)
   *
   * @param userId    user id
   * @param productId product id
   * @param amount    amount of product
   * @param handler   async handler
   */
  @Fluent
  ShopService buy(String userId, String productId, int amount, Handler<AsyncResult<JsonObject>> handler);

}

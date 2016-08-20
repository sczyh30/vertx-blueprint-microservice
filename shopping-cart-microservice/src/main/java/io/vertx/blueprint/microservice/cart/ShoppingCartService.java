package io.vertx.blueprint.microservice.cart;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * A service interface for shopping cart operation.
 * <p>
 * This service is an event bus service (aka. service proxy).
 * </p>
 *
 * @author Eric Zhao
 */
@VertxGen(concrete = false)
@ProxyGen
public interface ShoppingCartService {

  /**
   * The name of the event bus service.
   */
  String SERVICE_NAME = "shopping-cart-eb-service";

  /**
   * The address on which the service is published.
   */
  String SERVICE_ADDRESS = "service.shopping.cart";

  /**
   * Add cart event to the event source.
   *
   * @param event         cart event
   * @param resultHandler async result handler
   */
  @Fluent
  ShoppingCartService addCartEvent(CartEvent event, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Get shopping cart of a user.
   *
   * @param userId user id
   * @param resultHandler async result handler
   */
  @Fluent
  ShoppingCartService getShoppingCart(String userId, Handler<AsyncResult<ShoppingCart>> resultHandler);

}

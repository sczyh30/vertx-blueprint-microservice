package io.vertx.blueprint.microservice.cart;

import io.vertx.codegen.annotations.VertxGen;

/**
 * An enum class for the type of {@link CartEvent}.
 */
@VertxGen
public enum CartEventType {
  ADD_ITEM,
  REMOVE_ITEM,
  CHECKOUT,
  CLEAR_CART
}

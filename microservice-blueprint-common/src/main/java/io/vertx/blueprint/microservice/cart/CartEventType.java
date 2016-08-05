package io.vertx.blueprint.microservice.cart;

/**
 * An enum class for the type of {@link CartEvent}.
 */
public enum CartEventType {
  ADD_ITEM,
  REMOVE_ITEM,
  CHECKOUT,
  CLEAR_CART
}

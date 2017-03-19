package io.vertx.blueprint.microservice.cart;

import io.vertx.blueprint.microservice.product.ProductTuple;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Shopping cart state object.
 */
@DataObject(generateConverter = true)
public class ShoppingCart {

  private List<ProductTuple> productItems = new ArrayList<>();
  private Map<String, Integer> amountMap = new HashMap<>();

  public ShoppingCart() {
    // Empty constructor.
  }

  public ShoppingCart(JsonObject json) {
    ShoppingCartConverter.fromJson(json, this);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    ShoppingCartConverter.toJson(this, json);
    return json;
  }

  public List<ProductTuple> getProductItems() {
    return productItems;
  }

  public ShoppingCart setProductItems(List<ProductTuple> productItems) {
    this.productItems = productItems;
    return this;
  }

  @GenIgnore
  public Map<String, Integer> getAmountMap() {
    return amountMap;
  }

  public boolean isEmpty() {
    return productItems.isEmpty();
  }

  public ShoppingCart incorporate(CartEvent cartEvent) {
    // The cart event must be a add or remove command event.
    boolean ifValid = Stream.of(CartEventType.ADD_ITEM, CartEventType.REMOVE_ITEM)
      .anyMatch(cartEventType ->
        cartEvent.getCartEventType().equals(cartEventType));

    if (ifValid) {
      amountMap.put(cartEvent.getProductId(),
        amountMap.getOrDefault(cartEvent.getProductId(), 0) +
          (cartEvent.getAmount() * (cartEvent.getCartEventType()
            .equals(CartEventType.ADD_ITEM) ? 1 : -1)));
    }

    return this;
  }

  @Override
  public String toString() {
    return this.toJson().encode();
  }
}

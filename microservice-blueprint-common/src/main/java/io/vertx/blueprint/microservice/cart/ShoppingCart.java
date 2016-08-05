package io.vertx.blueprint.microservice.cart;

import io.vertx.blueprint.microservice.product.ProductTuple;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Shopping cart state object.
 */
@DataObject(generateConverter = true)
public class ShoppingCart {

  private List<ProductTuple> productItems = new ArrayList<>();
  private Map<String, Double> priceMap = new HashMap<>();

  public ShoppingCart() {
    // Empty constructor
  }

  public ShoppingCart(ShoppingCart other) {
    this.productItems = new ArrayList<>(other.productItems);
    this.priceMap = new HashMap<>(other.priceMap);
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

  public Map<String, Double> getPriceMap() {
    return priceMap;
  }

  public ShoppingCart setPriceMap(Map<String, Double> priceMap) {
    this.priceMap = priceMap;
    return this;
  }

  public boolean isEmpty() {
    return productItems.isEmpty();
  }

  @Override
  public String toString() {
    return this.toJson().encode();
  }
}

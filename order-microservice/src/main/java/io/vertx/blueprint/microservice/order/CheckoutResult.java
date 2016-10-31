package io.vertx.blueprint.microservice.order;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Checkout result data object.
 */
@DataObject(generateConverter = true)
public class CheckoutResult {

  private String message;
  private Order order;

  public CheckoutResult() {
    // Empty constructor
  }

  public CheckoutResult(String message, Order order) {
    this.message = message;
    this.order = order;
  }

  public CheckoutResult(JsonObject json) {
    CheckoutResultConverter.fromJson(json, this);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    CheckoutResultConverter.toJson(this, json);
    return json;
  }

  public String getMessage() {
    return message;
  }

  public CheckoutResult setMessage(String message) {
    this.message = message;
    return this;
  }

  public Order getOrder() {
    return order;
  }

  public CheckoutResult setOrder(Order order) {
    this.order = order;
    return this;
  }

  @Override
  public String toString() {
    return this.toJson().encode();
  }
}

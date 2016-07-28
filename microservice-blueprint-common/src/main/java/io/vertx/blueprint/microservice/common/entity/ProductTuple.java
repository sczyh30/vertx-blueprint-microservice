package io.vertx.blueprint.microservice.common.entity;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * A product tuple represents the amount of a certain product in a shopping.
 */
@DataObject(generateConverter = true)
public class ProductTuple /*extends Tuple2<String, Integer>*/ {

  private String productId;
  private Integer amount;

  public ProductTuple() {
    // empty constructor
  }

  public ProductTuple(String productId, Integer amount) {
    this.productId = productId;
    this.amount = amount;
  }

  public ProductTuple(JsonObject json) {
    ProductTupleConverter.fromJson(json, this);
  }

  public ProductTuple(ProductTuple other) {
    this.productId = other.productId;
    this.amount = other.amount;
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    ProductTupleConverter.toJson(this, json);
    return json;
  }

  public String getProductId() {
    return productId;
  }

  public ProductTuple setProductId(String productId) {
    this.productId = productId;
    return this;
  }

  public Integer getAmount() {
    return amount;
  }

  public ProductTuple setAmount(Integer amount) {
    this.amount = amount;
    return this;
  }

  @Override
  public String toString() {
    return "(" + productId + "," + amount + ")";
  }
}

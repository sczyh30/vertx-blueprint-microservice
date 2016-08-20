package io.vertx.blueprint.microservice.product;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * A product tuple represents the amount of a certain product in a shopping.
 */
@DataObject(generateConverter = true)
public class ProductTuple /*extends Tuple4<String, String, Double, Integer>*/ {

  private String productId;
  private String sellerId;
  private Double price;
  private Integer amount;

  public ProductTuple() {
    // empty constructor
  }

  public ProductTuple(String productId, String sellerId, Double price, Integer amount) {
    this.productId = productId;
    this.sellerId = sellerId;
    this.price = price;
    this.amount = amount;
  }

  public ProductTuple(Product product, Integer amount) {
    this.productId = product.getProductId();
    this.sellerId = product.getSellerId();
    this.price = product.getPrice();
    this.amount = amount;
  }

  public ProductTuple(JsonObject json) {
    ProductTupleConverter.fromJson(json, this);
  }

  public ProductTuple(ProductTuple other) {
    this.productId = other.productId;
    this.sellerId = other.sellerId;
    this.price = other.price;
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

  public String getSellerId() {
    return sellerId;
  }

  public ProductTuple setSellerId(String sellerId) {
    this.sellerId = sellerId;
    return this;
  }

  public Double getPrice() {
    return price;
  }

  public ProductTuple setPrice(Double price) {
    this.price = price;
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
    return "(" + productId + "," + sellerId + "," + amount + ")";
  }
}

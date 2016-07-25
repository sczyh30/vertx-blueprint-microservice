package io.vertx.blueprint.microservice.order;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Vert.x Blueprint - B2B Shopping Microservice
 * Order data object
 *
 * @author Eric Zhao
 */
@DataObject(generateConverter = true)
public class Order {

  private Long orderId;
  private String buyerId;
  private String productId;
  private String sellerId;

  private Long createTime;

  private Double totalPrice;

  public Order() {
    // Empty constructor
  }

  public Order(Order other) {
    this.orderId = other.orderId;
    this.buyerId = other.buyerId;
    this.productId = other.productId;
    this.sellerId = other.sellerId;
    this.createTime = other.createTime;
    this.totalPrice = other.totalPrice;
  }

  public Order(JsonObject json) {
    OrderConverter.fromJson(json, this);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    OrderConverter.toJson(this, json);
    return json;
  }

  public Long getOrderId() {
    return orderId;
  }

  public Order setOrderId(Long orderId) {
    this.orderId = orderId;
    return this;
  }

  public String getBuyerId() {
    return buyerId;
  }

  public Order setBuyerId(String buyerId) {
    this.buyerId = buyerId;
    return this;
  }

  public String getProductId() {
    return productId;
  }

  public Order setProductId(String productId) {
    this.productId = productId;
    return this;
  }

  public String getSellerId() {
    return sellerId;
  }

  public Order setSellerId(String sellerId) {
    this.sellerId = sellerId;
    return this;
  }

  public Long getCreateTime() {
    return createTime;
  }

  public Order setCreateTime(Long createTime) {
    this.createTime = createTime;
    return this;
  }

  public Double getTotalPrice() {
    return totalPrice;
  }

  public Order setTotalPrice(Double totalPrice) {
    this.totalPrice = totalPrice;
    return this;
  }
}

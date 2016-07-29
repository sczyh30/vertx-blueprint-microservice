package io.vertx.blueprint.microservice.order;

import io.vertx.blueprint.microservice.common.entity.ProductTuple;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Order data object.
 *
 * @author Eric Zhao
 */
@DataObject(generateConverter = true)
public class Order {

  private Long orderId = -1L;
  private String payId;
  private String buyerId;
  private String sellerId;

  private Long createTime;

  private List<ProductTuple> products = new ArrayList<>();
  private Double totalPrice;


  public Order() {
  }

  public Order(Order other) {
    this.orderId = other.orderId;
    this.payId = other.payId;
    this.buyerId = other.buyerId;
    this.products = new ArrayList<>(other.products);
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

  public String getPayId() {
    return payId;
  }

  public Order setPayId(String payId) {
    this.payId = payId;
    return this;
  }

  public String getBuyerId() {
    return buyerId;
  }

  public Order setBuyerId(String buyerId) {
    this.buyerId = buyerId;
    return this;
  }

  public String getSellerId() {
    return sellerId;
  }

  public Order setSellerId(String sellerId) {
    this.sellerId = sellerId;
    return this;
  }

  public List<ProductTuple> getProducts() {
    return products;
  }

  public Order setProducts(List<ProductTuple> products) {
    this.products = products;
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

  @Override
  public String toString() {
    return this.toJson().encodePrettily();
  }
}

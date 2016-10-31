package io.vertx.blueprint.microservice.order;

import io.vertx.blueprint.microservice.product.ProductTuple;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

  private Long createTime;

  private List<ProductTuple> products = new ArrayList<>();
  private Double totalPrice;


  public Order() {
  }

  public Order(Long orderId) {
    this.orderId = orderId;
  }

  public Order(JsonObject json) {
    OrderConverter.fromJson(json, this);
    if (json.getValue("products") instanceof String) {
      this.products = new JsonArray(json.getString("products"))
        .stream()
        .map(e -> (JsonObject) e)
        .map(ProductTuple::new)
        .collect(Collectors.toList());
    }
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

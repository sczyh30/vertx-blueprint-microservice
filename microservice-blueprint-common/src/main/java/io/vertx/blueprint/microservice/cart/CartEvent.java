package io.vertx.blueprint.microservice.cart;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Cart event state object.
 *
 * @author Eric Zhao
 */
@DataObject(generateConverter = true)
public class CartEvent {

  private Long id;
  private CartEventType cartEventType;
  private String userId;
  private String productId;
  private Integer amount;

  private long createdAt;

  public CartEvent() {
    this.createdAt = System.currentTimeMillis();
  }

  public CartEvent(JsonObject json) {
    CartEventConverter.fromJson(json, this);
  }

  public CartEvent(CartEventType cartEventType, String userId, String productId, Integer amount) {
    this.cartEventType = cartEventType;
    this.userId = userId;
    this.productId = productId;
    this.amount = amount;
    this.createdAt = System.currentTimeMillis();
  }

  /**
   * Helper method to create checkout event for a user.
   *
   * @param userId user id
   * @return created checkout cart event
   */
  public static CartEvent createCheckoutEvent(String userId) {
    return new CartEvent(CartEventType.CHECKOUT, userId, "all", 0);
  }

  /**
   * Helper method to create clear cart event for a user.
   *
   * @param userId user id
   * @return created clear cart event
   */
  public static CartEvent createClearEvent(String userId) {
    return new CartEvent(CartEventType.CLEAR_CART, userId, "all", 0);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    CartEventConverter.toJson(this, json);
    return json;
  }

  public Long getId() {
    return id;
  }

  public CartEvent setId(Long id) {
    this.id = id;
    return this;
  }

  public CartEventType getCartEventType() {
    return cartEventType;
  }

  public CartEvent setCartEventType(CartEventType cartEventType) {
    this.cartEventType = cartEventType;
    return this;
  }

  public String getUserId() {
    return userId;
  }

  public CartEvent setUserId(String userId) {
    this.userId = userId;
    return this;
  }

  public String getProductId() {
    return productId;
  }

  public CartEvent setProductId(String productId) {
    this.productId = productId;
    return this;
  }

  public Integer getAmount() {
    return amount;
  }

  public CartEvent setAmount(Integer amount) {
    this.amount = amount;
    return this;
  }

  public Long getCreatedAt() {
    return createdAt;
  }

  public CartEvent setCreatedAt(Long createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  @Override
  public String toString() {
    return this.toJson().encode();
  }

  public static boolean isTerminal(CartEventType eventType) {
    return eventType == CartEventType.CLEAR_CART || eventType == CartEventType.CHECKOUT;
  }
}

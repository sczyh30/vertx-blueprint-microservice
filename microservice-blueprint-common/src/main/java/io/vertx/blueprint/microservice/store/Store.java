package io.vertx.blueprint.microservice.store;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Online store data object.
 *
 * @author Eric Zhao
 */
@DataObject(generateConverter = true)
public class Store {

  private String sellerId;
  private String name;
  private String description;
  private Long openTime;

  public Store() {
    this.openTime = System.currentTimeMillis();
  }

  public Store(Store other) {
    this.openTime = other.openTime;
    this.description = other.description;
    this.name = other.name;
    this.sellerId = other.sellerId;
  }

  public Store(JsonObject json) {
    StoreConverter.fromJson(json, this);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    StoreConverter.toJson(this, json);
    return json;
  }

  public String getSellerId() {
    return sellerId;
  }

  public Store setSellerId(String sellerId) {
    this.sellerId = sellerId;
    return this;
  }

  public String getName() {
    return name;
  }

  public Store setName(String name) {
    this.name = name;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public Store setDescription(String description) {
    this.description = description;
    return this;
  }

  public Long getOpenTime() {
    return openTime;
  }

  public Store setOpenTime(Long openTime) {
    this.openTime = openTime;
    return this;
  }

  @Override
  public String toString() {
    return this.toJson().encodePrettily();
  }
}

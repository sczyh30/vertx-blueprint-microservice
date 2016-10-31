package io.vertx.blueprint.microservice.product;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * Product data object.
 *
 * @author Eric Zhao
 */
@DataObject(generateConverter = true)
public class Product {

  private String productId;
  private String sellerId;
  private String name;
  private double price = 0.0d;
  private String illustration;
  private String type;

  public Product() {
    // Empty constructor
  }

  public Product(Product other) {
    this.productId = other.productId;
    this.sellerId = other.sellerId;
    this.name = other.name;
    this.price = other.price;
    this.illustration = other.illustration;
    this.type = other.type;
  }

  public Product(JsonObject json) {
    ProductConverter.fromJson(json, this);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    ProductConverter.toJson(this, json);
    return json;
  }

  public String getProductId() {
    return productId;
  }

  public Product setProductId(String productId) {
    this.productId = productId;
    return this;
  }

  public String getSellerId() {
    return sellerId;
  }

  public Product setSellerId(String sellerId) {
    this.sellerId = sellerId;
    return this;
  }

  public String getName() {
    return name;
  }

  public Product setName(String name) {
    this.name = name;
    return this;
  }

  public double getPrice() {
    return price;
  }

  public Product setPrice(double price) {
    this.price = price;
    return this;
  }

  public String getIllustration() {
    return illustration;
  }

  public Product setIllustration(String illustration) {
    this.illustration = illustration;
    return this;
  }

  public String getType() {
    return type;
  }

  public Product setType(String type) {
    this.type = type;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Product product = (Product) o;

    return productId.equals(product.productId) && sellerId.equals(product.sellerId);
  }

  @Override
  public int hashCode() {
    int result = productId.hashCode();
    result = 31 * result + sellerId.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return this.toJson().encodePrettily();
  }
}

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
  private String name;
  private double price = 0.0d;
  private String illustration;

  public Product() {
    // Empty constructor
  }

  public Product(Product other) {
    this.productId = other.productId;
    this.name = other.name;
    this.price = other.price;
    this.illustration = other.illustration;
  }

  public Product(JsonObject json) {
    ProductConverter.fromJson(json, this);
  }

  public String getProductId() {
    return productId;
  }

  public Product setProductId(String productId) {
    this.productId = productId;
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
}

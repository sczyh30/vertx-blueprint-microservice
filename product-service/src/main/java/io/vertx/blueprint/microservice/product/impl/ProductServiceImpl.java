package io.vertx.blueprint.microservice.product.impl;

import io.vertx.blueprint.microservice.product.Product;
import io.vertx.blueprint.microservice.product.ProductService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.List;

/**
 * JDBC implementation of {@link io.vertx.blueprint.microservice.product.ProductService}.
 *
 * @author Eric Zhao
 */
public class ProductServiceImpl implements ProductService {

  private final Vertx vertx;
  private final JDBCClient jdbc;

  public ProductServiceImpl(Vertx vertx, JsonObject config) {
    this.vertx = vertx;
    this.jdbc = JDBCClient.createNonShared(vertx, config);
  }

  @Override
  public ProductService initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
    return null;
  }

  @Override
  public ProductService addProduct(Product product, Handler<AsyncResult<Void>> resultHandler) {
    return null;
  }

  @Override
  public ProductService retrieveProduct(String productId, Handler<AsyncResult<Product>> resultHandler) {
    return null;
  }

  @Override
  public ProductService retrieveAllProducts(Handler<AsyncResult<List<Product>>> resultHandler) {
    return null;
  }

  @Override
  public ProductService deleteProduct(String productId, Handler<AsyncResult<Void>> resultHandler) {
    return null;
  }

  @Override
  public ProductService deleteAllProducts(Handler<AsyncResult<Void>> resultHandler) {
    return null;
  }
}

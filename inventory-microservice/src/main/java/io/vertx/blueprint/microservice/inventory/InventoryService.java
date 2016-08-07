package io.vertx.blueprint.microservice.inventory;

import io.vertx.blueprint.microservice.inventory.impl.InventoryServiceImpl;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Inventory service (asynchronous based on Future).
 */
public interface InventoryService {

  /**
   * Create a new inventory service instance.
   *
   * @param vertx  Vertx instance
   * @param config configuration object
   * @return a new inventory service instance
   */
  static InventoryService createService(Vertx vertx, JsonObject config) {
    return new InventoryServiceImpl(vertx, config);
  }

  /**
   * Increase the inventory amount of a certain product.
   *
   * @param productId the id of the product
   * @param increase  increase amount
   * @return the asynchronous result
   */
  Future<Integer> increase(String productId, int increase);

  /**
   * Decrease the inventory amount of a certain product.
   *
   * @param productId the id of the product
   * @param decrease  decrease amount
   * @return the asynchronous result
   */
  Future<Integer> decrease(String productId, int decrease);

  /**
   * Retrieve the inventory amount of a certain product.
   *
   * @param productId the id of the product
   * @return the asynchronous result
   */
  Future<Integer> retrieveInventoryForProduct(String productId);

}

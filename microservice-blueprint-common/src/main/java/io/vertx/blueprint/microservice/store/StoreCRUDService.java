package io.vertx.blueprint.microservice.store;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * A service interface for online store CURD operation.
 * <p>
 * This service is an event bus service (aka. service proxy).
 * </p>
 *
 * @author Eric Zhao
 */
@VertxGen
@ProxyGen
public interface StoreCRUDService {

  String SERVICE_NAME = "store-eb-service";

  String SERVICE_ADDRESS = "service.store";

  /**
   * Save an online store to the persistence layer. This is a so called `upsert` operation.
   * This is used to update store info, or just apply for a new store.
   *
   * @param store         store object
   * @param resultHandler async result handler
   */
  void saveStore(Store store, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Retrieve an online store by seller id.
   *
   * @param sellerId seller id, refers to an independent online store
   * @param resultHandler async result handler
   */
  void retrieveStore(String sellerId, Handler<AsyncResult<Store>> resultHandler);

  /**
   * Remove an online store whose seller is {@code sellerId}.
   * This is used to close an online store.
   *
   * @param sellerId seller id, refers to an independent online store
   * @param resultHandler async result handler
   */
  void removeStore(String sellerId, Handler<AsyncResult<Void>> resultHandler);

}

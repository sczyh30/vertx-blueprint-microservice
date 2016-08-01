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

  void saveStore(Store store, Handler<AsyncResult<Void>> resultHandler);

  void retrieveStore(String sellerId, Handler<AsyncResult<Store>> resultHandler);

  void removeStore(String sellerId, Handler<AsyncResult<Void>> resultHandler);

}

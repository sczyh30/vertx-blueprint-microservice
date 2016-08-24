package io.vertx.blueprint.microservice.store.impl;

import io.vertx.blueprint.microservice.store.Store;
import io.vertx.blueprint.microservice.store.StoreCRUDService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/**
 * Implementation of {@link StoreCRUDService}. Use MongoDB as the persistence.
 */
public class StoreCRUDServiceImpl implements StoreCRUDService {

  private static final String COLLECTION = "store";

  private final MongoClient client;

  public StoreCRUDServiceImpl(Vertx vertx, JsonObject config) {
    this.client = MongoClient.createNonShared(vertx, config);
  }

  @Override
  public void saveStore(Store store, Handler<AsyncResult<Void>> resultHandler) {
    client.save(COLLECTION, new JsonObject().put("_id", store.getSellerId())
        .put("name", store.getName())
        .put("description", store.getDescription())
        .put("openTime", store.getOpenTime()),
      ar -> {
        if (ar.succeeded()) {
          resultHandler.handle(Future.succeededFuture());
        } else {
          resultHandler.handle(Future.failedFuture(ar.cause()));
        }
      }
    );
  }

  @Override
  public void retrieveStore(String sellerId, Handler<AsyncResult<Store>> resultHandler) {
    JsonObject query = new JsonObject().put("_id", sellerId);
    client.findOne(COLLECTION, query, null, ar -> {
      if (ar.succeeded()) {
        if (ar.result() == null) {
          resultHandler.handle(Future.succeededFuture());
        } else {
          Store store = new Store(ar.result().put("sellerId", ar.result().getString("_id")));
          resultHandler.handle(Future.succeededFuture(store));
        }
      } else {
        resultHandler.handle(Future.failedFuture(ar.cause()));
      }
    });
  }

  @Override
  public void removeStore(String sellerId, Handler<AsyncResult<Void>> resultHandler) {
    JsonObject query = new JsonObject().put("_id", sellerId);
    client.removeDocument(COLLECTION, query, ar -> {
      if (ar.succeeded()) {
        resultHandler.handle(Future.succeededFuture());
      } else {
        resultHandler.handle(Future.failedFuture(ar.cause()));
      }
    });
  }
}

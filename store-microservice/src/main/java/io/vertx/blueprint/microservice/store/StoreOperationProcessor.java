package io.vertx.blueprint.microservice.store;

import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.blueprint.microservice.common.functional.Tuple2;
import io.vertx.blueprint.microservice.store.impl.StoreCRUDServiceImpl;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.types.MessageSource;
import io.vertx.serviceproxy.ProxyHelper;

/**
 * A verticle for store operation (e.g. apply or close) processing.
 *
 * @author Eric Zhao
 */
public class StoreOperationProcessor extends BaseMicroserviceVerticle {

  public static final String SERVICE_NAME = "store-eb-service";
  public static final String SERVICE_ADDRESS = "service.store";

  private StoreCRUDService crudService;

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();
    crudService = new StoreCRUDServiceImpl(vertx, config());
    ProxyHelper.registerService(StoreCRUDService.class, vertx, crudService, SERVICE_ADDRESS);
    publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, StoreCRUDService.class)
      .compose(servicePublished -> prepareProcessing())
      .setHandler(future.completer());
  }

  private Future<Void> prepareProcessing() {
    Future<Void> future = Future.future();
    MessageSource.<JsonObject>getConsumer(discovery,
      new JsonObject().put("name", "store-op-message-source"),
      ar -> {
        if (ar.succeeded()) {
          MessageConsumer<JsonObject> opConsumer = ar.result();
          opConsumer.handler(this::processOperation);
          future.complete();
        } else {
          future.fail(ar.cause());
        }
      });
    return future;
  }

  private void processOperation(Message<JsonObject> message) {
    String opType = message.headers().get("type");
    Store store = new Store(message.body());
    switch (opType) {
      case "APPLY":
      case "UPDATE":
        processApplication(store);
        break;
      default:
        throw new RuntimeException("Unknown store operation");
    }
  }

  private void processApplication(Store store) {
    verifyStore(store).compose(verifyResult -> {
      JsonObject reply = new JsonObject().put("sellerId", store.getSellerId());
      if (verifyResult._1) {
        reply.put("status", 1)
          .put("store", store.toJson());
        Future<Void> future = Future.future();
        crudService.saveStore(store, future.completer());
        return future.map(r -> reply);
      } else {
        reply.put("status", 0)
          .put("reason", verifyResult._2);
        return Future.succeededFuture(reply);
      }
    }).setHandler(ar -> {

    });
  }

  private void pushStoreMessage(JsonObject reply) {
    // TODO: push message to the front-end microservice
  }

  /**
   * Verify the legality of the store.
   *
   * @param store store data object
   */
  private Future<Tuple2<Boolean, String>> verifyStore(Store store) {
    return Future.succeededFuture(new Tuple2<>(true, null)); // In this simulation we always return true :-)
  }
}

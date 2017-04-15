package io.vertx.blueprint.microservice.store.api;

import io.vertx.blueprint.microservice.common.RestAPIVerticle;
import io.vertx.blueprint.microservice.store.Store;
import io.vertx.blueprint.microservice.store.StoreCRUDService;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * A verticle provides REST API for online store service.
 */
public class RestStoreAPIVerticle extends RestAPIVerticle {

  private static final String SERVICE_NAME = "shop-rest-api";

  private static final String API_SAVE = "/save";
  private static final String API_RETRIEVE = "/:sellerId";
  private static final String API_CLOSE = "/:sellerId";

  private final StoreCRUDService service;

  public RestStoreAPIVerticle(StoreCRUDService service) {
    this.service = service;
  }

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();

    Router router = Router.router(vertx);
    // body handler
    router.route().handler(BodyHandler.create());
    // API route handler
    router.post(API_SAVE).handler(this::apiSave);
    router.get(API_RETRIEVE).handler(this::apiRetrieve);
    router.delete(API_CLOSE).handler(this::apiClose);

    // get HTTP host and port from configuration, or use default value
    String host = config().getString("store.http.address", "0.0.0.0");
    int port = config().getInteger("store.http.port", 8085);

    createHttpServer(router, host, port)
      .compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, host, port))
      .setHandler(future.completer());
  }

  private void apiSave(RoutingContext context) {
    Store store = new Store(new JsonObject(context.getBodyAsString()));
    if (store.getSellerId() == null) {
      badRequest(context, new IllegalStateException("Seller id does not exist"));
    } else {
      JsonObject result = new JsonObject().put("message", "store_saved")
        .put("sellerId", store.getSellerId());
      service.saveStore(store, resultVoidHandler(context, result));
    }
  }

  private void apiRetrieve(RoutingContext context) {
    String sellerId = context.request().getParam("sellerId");
    service.retrieveStore(sellerId, resultHandlerNonEmpty(context));
  }

  private void apiClose(RoutingContext context) {
    String sellerId = context.request().getParam("sellerId");
    service.removeStore(sellerId, deleteResultHandler(context));
  }
}

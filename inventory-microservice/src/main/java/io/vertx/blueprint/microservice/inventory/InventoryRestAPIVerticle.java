package io.vertx.blueprint.microservice.inventory;

import io.vertx.blueprint.microservice.common.RestAPIVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * A verticle supplies HTTP endpoint for inventory service API.
 *
 * @author Eric Zhao
 */
public class InventoryRestAPIVerticle extends RestAPIVerticle {

  private static final String SERVICE_NAME = "inventory-rest-api";

  private static final String API_INCREASE = "/inventory/:productId/increase";
  private static final String API_DECREASE = "/inventory/:productId/decrease";
  private static final String API_RETRIEVE = "/inventory/:productId";

  private InventoryService inventoryService;

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();

    this.inventoryService = InventoryService.createService(vertx, config());

    final Router router = Router.router(vertx);
    // body handler
    router.route().handler(BodyHandler.create());
    // API handler
    router.get(API_RETRIEVE).handler(this::apiRetrieve);
    router.put(API_INCREASE).handler(this::apiIncrease);
    router.put(API_DECREASE).handler(this::apiDecrease);

    // enable heart beat check
    enableHeartbeatCheck(router, config());

    // get HTTP host and port from configuration, or use default value
    String host = config().getString("inventory.http.address", "0.0.0.0");
    int port = config().getInteger("inventory.http.port", 8086);

    createHttpServer(router, host, port)
      .compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, host, port))
      .setHandler(future.completer());
  }

  private void apiIncrease(RoutingContext context) {
    try {
      String productId = context.request().getParam("productId");
      int increase = Integer.valueOf(context.request().getParam("n"));
      if (increase <= 0) {
        badRequest(context, new IllegalStateException("Negative increase"));
      } else {
        inventoryService.increase(productId, increase)
          .setHandler(resultHandler(context));
      }
    } catch (Exception ex) {
      badRequest(context, ex);
    }
  }

  private void apiDecrease(RoutingContext context) {
    try {
      String productId = context.request().getParam("productId");
      int decrease = Integer.valueOf(context.request().getParam("n"));
      if (decrease <= 0) {
        badRequest(context, new IllegalStateException("Negative decrease"));
      } else {
        inventoryService.decrease(productId, decrease)
          .setHandler(resultHandler(context));
      }
    } catch (NumberFormatException ex) {
      notFound(context);
    }
  }

  private void apiRetrieve(RoutingContext context) {
    String productId = context.request().getParam("productId");
    inventoryService.retrieveInventoryForProduct(productId)
      .setHandler(rawResultHandler(context));
  }

}

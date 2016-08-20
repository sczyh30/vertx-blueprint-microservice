package io.vertx.blueprint.microservice.order.api;

import io.vertx.blueprint.microservice.common.RestAPIVerticle;
import io.vertx.blueprint.microservice.order.OrderService;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * A verticle supplies REST endpoint for order service.
 *
 * @author Eric Zhao
 */
public class RestOrderAPIVerticle extends RestAPIVerticle {

  private static final String SERVICE_NAME = "order-rest-api";

  private static final String API_RETRIEVE = "/orders/:orderId";
  private static final String API_RETRIEVE_FOR_ACCOUNT = "/user/:id/orders";

  private final OrderService service;

  public RestOrderAPIVerticle(OrderService service) {
    this.service = service;
  }

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();

    Router router = Router.router(vertx);
    // body handler
    router.route().handler(BodyHandler.create());
    // API route
    router.get(API_RETRIEVE).handler(this::apiRetrieve);
    router.get(API_RETRIEVE_FOR_ACCOUNT).handler(this::apiRetrieveForAccount);

    String host = config().getString("order.http.address", "0.0.0.0");
    int port = config().getInteger("order.http.port", 8090);

    // create HTTP server and publish REST service
    createHttpServer(router, host, port)
      .compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, host, port))
      .setHandler(future.completer());
  }

  private void apiRetrieve(RoutingContext context) {
    try {
      Long orderId = Long.parseLong(context.request().getParam("orderId"));
      service.retrieveOrder(orderId, resultHandlerNonEmpty(context));
    } catch (NumberFormatException ex) {
      notFound(context);
    }
  }

  private void apiRetrieveForAccount(RoutingContext context) {
    String userId = context.request().getParam("id");
    service.retrieveOrdersForAccount(userId, resultHandler(context, Json::encodePrettily));
  }
}

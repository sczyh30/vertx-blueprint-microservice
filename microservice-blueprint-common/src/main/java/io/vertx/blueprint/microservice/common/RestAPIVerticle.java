package io.vertx.blueprint.microservice.common;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * An abstract base verticle that provides several helper methods for REST API.
 */
public abstract class RestAPIVerticle extends BaseMicroserviceVerticle {

  /**
   * This method generates handler for async methods in REST APIs.
   */
  protected <T> Handler<AsyncResult<T>> resultHandler(RoutingContext context, Handler<T> handler) {
    return res -> {
      if (res.succeeded()) {
        handler.handle(res.result());
      } else {
        serviceUnavailable(context, res.cause());
      }
    };
  }

  protected Handler<AsyncResult<Void>> deleteResultHandler(RoutingContext context) {
    return res -> {
      if (res.succeeded()) {
        context.response().setStatusCode(204)
          .putHeader("content-type", "application/json")
          .end(new JsonObject().put("message", "delete_success").encodePrettily());
      } else {
        serviceUnavailable(context);
      }
    };
  }

  protected void sendError(int statusCode, RoutingContext context) {
    context.response().setStatusCode(statusCode).end();
  }

  protected void badRequest(RoutingContext context, Throwable ex) {
    context.response().setStatusCode(400)
      .putHeader("content-type", "application/json")
      .end(new JsonObject().put("error", ex.getMessage()).encodePrettily());
  }

  protected void notFound(RoutingContext context) {
    context.response().setStatusCode(404)
      .putHeader("content-type", "application/json")
      .end(new JsonObject().put("message", "not_found").encodePrettily());
  }

  protected void notImplemented(RoutingContext context) {
    context.response().setStatusCode(501)
      .putHeader("content-type", "application/json")
      .end(new JsonObject().put("message", "not_implemented").encodePrettily());
  }

  protected void serviceUnavailable(RoutingContext context) {
    context.response().setStatusCode(503).end();
  }

  protected void serviceUnavailable(RoutingContext context, Throwable ex) {
    context.response().setStatusCode(503)
      .putHeader("content-type", "application/json")
      .end(new JsonObject().put("error", ex.getMessage()).encodePrettily());
  }

}

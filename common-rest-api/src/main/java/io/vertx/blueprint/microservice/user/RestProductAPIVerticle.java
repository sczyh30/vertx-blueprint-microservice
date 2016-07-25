package io.vertx.blueprint.microservice.user;

import io.vertx.blueprint.microservice.common.RestAPIVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * This verticle exposes a HTTP endpoint to process shopping products management with REST APIs.
 *
 * @author Eric Zhao
 */
public class RestProductAPIVerticle extends RestAPIVerticle {

  public static final String API_ADD = "/product";
  public static final String API_RETRIEVE = "/product/:productId";
  public static final String API_RETRIEVE_ALL = "/product";
  public static final String API_UPDATE = "/product/:productId";
  public static final String API_DELETE = "/product/:productId";
  public static final String API_DELETE_ALL = "/product";

  @Override
  public void start(Future<Void> future) throws Exception {
    final Router router = Router.router(vertx);
    // body handler
    router.route().handler(BodyHandler.create());
    // api route handler
    router.put(API_ADD).handler(this::apiAdd);
    router.get(API_RETRIEVE).handler(this::apiRetrieve);
    router.get(API_RETRIEVE_ALL).handler(this::apiRetrieveAll);
    router.patch(API_UPDATE).handler(this::apiUpdate);
    router.delete(API_DELETE).handler(this::apiDelete);
    router.delete(API_DELETE_ALL).handler(this::apiDeleteAll);

    // create http server for the REST service
    vertx.createHttpServer()
      .requestHandler(router::accept)
      .listen(config().getInteger("user.http.port", 8082),
        config().getString("user.http.address", "0.0.0.0"), result -> {
          if (result.succeeded()) {
            future.complete();
          } else {
            future.fail(result.cause());
          }
        });
  }

  private void apiAdd(RoutingContext context) {

  }

  private void apiRetrieve(RoutingContext context) {

  }

  private void apiRetrieveAll(RoutingContext context) {

  }

  private void apiUpdate(RoutingContext context) {

  }

  private void apiDelete(RoutingContext context) {

  }

  private void apiDeleteAll(RoutingContext context) {

  }

}

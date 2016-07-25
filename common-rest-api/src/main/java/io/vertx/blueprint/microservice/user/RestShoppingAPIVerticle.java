package io.vertx.blueprint.microservice.user;

import io.vertx.blueprint.microservice.common.RestAPIVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * This verticle exposes a HTTP endpoint to process shopping transactions with REST APIs.
 *
 * @author Eric Zhao
 */
public class RestShoppingAPIVerticle extends RestAPIVerticle {

  public static final String API_BUY = "/shop/:productId";

  @Override
  public void start(Future<Void> future) throws Exception {
    final Router router = Router.router(vertx);
    // body handler
    router.route().handler(BodyHandler.create());
    // api route handler
    router.put(API_BUY).handler(this::apiBuy);

    // create http server for the REST service
    vertx.createHttpServer()
      .requestHandler(router::accept)
      .listen(config().getInteger("user.http.port", 8081),
        config().getString("user.http.address", "0.0.0.0"), result -> {
          if (result.succeeded()) {
            future.complete();
          } else {
            future.fail(result.cause());
          }
        });
  }

  private void apiBuy(RoutingContext context) {

  }

}

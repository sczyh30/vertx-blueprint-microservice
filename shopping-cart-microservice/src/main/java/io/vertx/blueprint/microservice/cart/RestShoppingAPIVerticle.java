package io.vertx.blueprint.microservice.cart;

import io.vertx.blueprint.microservice.common.RestAPIVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * This verticle exposes a HTTP endpoint to process shopping cart with REST APIs.
 *
 * @author Eric Zhao
 */
public class RestShoppingAPIVerticle extends RestAPIVerticle {

  private static final String API_BUY = "/od_submit";

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();
    final Router router = Router.router(vertx);
    // body handler
    router.route().handler(BodyHandler.create());
    // api route handler
    router.post(API_BUY).handler(this::apiBuy);

    // create http server for the REST service
    vertx.createHttpServer()
      .requestHandler(router::accept)
      .listen(config().getInteger("shopping.cart.http.port", 8084),
        config().getString("shopping.cart.http.address", "0.0.0.0"), result -> {
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

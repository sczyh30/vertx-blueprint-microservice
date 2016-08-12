package io.vertx.blueprint.microservice.cart.api;

import io.vertx.blueprint.microservice.cart.CheckoutService;
import io.vertx.blueprint.microservice.cart.ShoppingCartService;
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

  private final ShoppingCartService shoppingCartService;
  private final CheckoutService checkoutService;

  private static final String API_BUY = "/cart/submit";

  public RestShoppingAPIVerticle(ShoppingCartService shoppingCartService, CheckoutService checkoutService) {
    this.shoppingCartService = shoppingCartService;
    this.checkoutService = checkoutService;
  }

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();
    final Router router = Router.router(vertx);
    // body handler
    router.route().handler(BodyHandler.create());
    // api route handler
    router.post(API_BUY).handler(this::apiBuy);

    enableHeartbeatCheck(router, config());

    // get HTTP host and port from configuration, or use default value
    String host = config().getString("shopping.cart.http.address", "0.0.0.0");
    int port = config().getInteger("shopping.cart.http.port", 8084);

    // create http server for the REST service
    createHttpServer(router, host, port)
      .setHandler(future.completer());
  }

  private void apiBuy(RoutingContext context) {
    notImplemented(context);
  }

}

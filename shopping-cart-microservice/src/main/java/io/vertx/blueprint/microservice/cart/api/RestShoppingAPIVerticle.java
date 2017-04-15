package io.vertx.blueprint.microservice.cart.api;

import io.vertx.blueprint.microservice.cart.CartEvent;
import io.vertx.blueprint.microservice.cart.CheckoutService;
import io.vertx.blueprint.microservice.cart.ShoppingCartService;
import io.vertx.blueprint.microservice.common.RestAPIVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.Optional;

/**
 * This verticle exposes a HTTP endpoint to process shopping cart with REST APIs.
 *
 * @author Eric Zhao
 */
public class RestShoppingAPIVerticle extends RestAPIVerticle {

  private static final String SERVICE_NAME = "shopping-cart-rest-api";

  private final ShoppingCartService shoppingCartService;
  private final CheckoutService checkoutService;

  private static final String API_CHECKOUT = "/checkout";
  private static final String API_ADD_CART_EVENT = "/events";
  private static final String API_GET_CART = "/cart";

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
    router.post(API_CHECKOUT).handler(context -> requireLogin(context, this::apiCheckout));
    router.post(API_ADD_CART_EVENT).handler(context -> requireLogin(context, this::apiAddCartEvent));
    router.get(API_GET_CART).handler(context -> requireLogin(context, this::apiGetCart));

    enableLocalSession(router);

    // get HTTP host and port from configuration, or use default value
    String host = config().getString("shopping.cart.http.address", "0.0.0.0");
    int port = config().getInteger("shopping.cart.http.port", 8084);

    // create http server for the REST service
    createHttpServer(router, host, port)
      .compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, host, port))
      .setHandler(future.completer());
  }

  private void apiCheckout(RoutingContext context, JsonObject principle) {
    String userId = Optional.ofNullable(principle.getString("userId"))
      .orElse(TEST_USER);
    checkoutService.checkout(userId, resultHandler(context));
  }

  private void apiAddCartEvent(RoutingContext context, JsonObject principal) {
    String userId = Optional.ofNullable(principal.getString("userId"))
      .orElse(TEST_USER);
    CartEvent cartEvent = new CartEvent(context.getBodyAsJson());
    if (validateEvent(cartEvent, userId)) {
      shoppingCartService.addCartEvent(cartEvent, resultVoidHandler(context, 201));
    } else {
      context.fail(400);
    }
  }

  private void apiGetCart(RoutingContext context, JsonObject principal) {
    String userId = Optional.ofNullable(principal.getString("userId"))
      .orElse(TEST_USER);
    shoppingCartService.getShoppingCart(userId, resultHandler(context));
  }

  private boolean validateEvent(CartEvent event, String userId) {
    return event.getUserId() != null && event.getAmount() != null && event.getAmount() > 0
      && event.getUserId().equals(userId);
  }

  // for test
  private static final String TEST_USER = "TEST666";

}

package io.vertx.blueprint.microservice.cart;

import io.vertx.blueprint.microservice.cart.api.RestShoppingAPIVerticle;
import io.vertx.blueprint.microservice.cart.impl.ShoppingCartServiceImpl;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * Shopping cart verticle.
 *
 * @author Eric Zhao
 */
public class CartVerticle extends SimpleCheckoutVerticle {

  private ShoppingCartService shoppingCartService;
  private CheckoutService checkoutService;

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start(future);

    this.shoppingCartService = new ShoppingCartServiceImpl(vertx, discovery, config());
    this.checkoutService = CheckoutService.createService(vertx, discovery);

    deployRestVerticle(config())
      .compose(restDeployed ->
        publishMessageSource("shopping-payment-message-source", CheckoutService.PAYMENT_EVENT_ADDRESS))
      .compose(servicePublished ->
        publishMessageSource("shopping-order-message-source", CheckoutService.ORDER_EVENT_ADDRESS))
      .setHandler(future.completer());
  }

  private Future<Void> deployRestVerticle(JsonObject config) {
    Future<String> future = Future.future();
    vertx.deployVerticle(new RestShoppingAPIVerticle(shoppingCartService, checkoutService),
      new DeploymentOptions().setConfig(config),
      future.completer());
    return future.map(r -> null);
  }


}

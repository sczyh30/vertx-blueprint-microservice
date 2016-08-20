package io.vertx.blueprint.microservice.cart;

import io.vertx.blueprint.microservice.cart.api.RestShoppingAPIVerticle;
import io.vertx.blueprint.microservice.cart.impl.ShoppingCartServiceImpl;
import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.serviceproxy.ProxyHelper;

/**
 * Shopping cart verticle.
 *
 * @author Eric Zhao
 */
public class CartVerticle extends BaseMicroserviceVerticle {

  private ShoppingCartService shoppingCartService;
  private CheckoutService checkoutService;

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();

    // create the service instance
    this.shoppingCartService = new ShoppingCartServiceImpl(vertx, discovery, config());
    this.checkoutService = CheckoutService.createService(vertx, discovery);
    // register the service proxy on event bus
    ProxyHelper.registerService(CheckoutService.class, vertx, checkoutService, CheckoutService.SERVICE_ADDRESS);
    ProxyHelper.registerService(ShoppingCartService.class, vertx, shoppingCartService, ShoppingCartService.SERVICE_ADDRESS);

    // publish the service in the discovery infrastructure
    publishEventBusService(CheckoutService.SERVICE_NAME, CheckoutService.SERVICE_ADDRESS, CheckoutService.class)
      .compose(servicePublished ->
        publishEventBusService(ShoppingCartService.SERVICE_NAME, ShoppingCartService.SERVICE_ADDRESS, ShoppingCartService.class))
      .compose(servicePublished ->
        publishMessageSource("shopping-payment-message-source", CheckoutService.PAYMENT_EVENT_ADDRESS))
      .compose(sourcePublished ->
        publishMessageSource("shopping-order-message-source", CheckoutService.ORDER_EVENT_ADDRESS))
      .compose(sourcePublished -> deployRestVerticle())
      .setHandler(future.completer());
  }

  private Future<Void> deployRestVerticle() {
    Future<String> future = Future.future();
    vertx.deployVerticle(new RestShoppingAPIVerticle(shoppingCartService, checkoutService),
      new DeploymentOptions().setConfig(config()),
      future.completer());
    return future.map(r -> null);
  }

}

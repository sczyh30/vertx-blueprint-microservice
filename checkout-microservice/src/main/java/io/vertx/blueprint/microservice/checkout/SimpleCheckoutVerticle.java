package io.vertx.blueprint.microservice.checkout;

import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.core.Future;
import io.vertx.serviceproxy.ProxyHelper;

/**
 * A simple verticle for shopping checkout service.
 *
 * @author Eric Zhao
 */
public class SimpleCheckoutVerticle extends BaseMicroserviceVerticle {

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();

    // create the service instance
    CheckoutService checkoutService = CheckoutService.createService(vertx, discovery);
    // register the service proxy on event bus
    ProxyHelper.registerService(CheckoutService.class, vertx, checkoutService, CheckoutService.SERVICE_ADDRESS);
    // publish the service in the discovery infrastructure
    publishEventBusService(CheckoutService.SERVICE_NAME, CheckoutService.SERVICE_ADDRESS, CheckoutService.class)
      .compose(servicePublished ->
        publishMessageSource("shopping-payment-message-source", CheckoutService.PAYMENT_EVENT_ADDRESS))
      .compose(servicePublished ->
        publishMessageSource("shopping-order-message-source", CheckoutService.ORDER_EVENT_ADDRESS))
      .setHandler(future.completer());
  }
}

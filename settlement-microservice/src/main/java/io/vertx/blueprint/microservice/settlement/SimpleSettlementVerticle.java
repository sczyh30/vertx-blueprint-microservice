package io.vertx.blueprint.microservice.settlement;

import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.core.Future;
import io.vertx.serviceproxy.ProxyHelper;

/**
 * A simple verticle for shopping service.
 *
 * @author Eric Zhao
 */
public class SimpleSettlementVerticle extends BaseMicroserviceVerticle {

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();

    // create the service instance
    SettlementService settlementService = SettlementService.createService(vertx, discovery);
    // register the service proxy on event bus
    ProxyHelper.registerService(SettlementService.class, vertx, settlementService, SettlementService.SERVICE_ADDRESS);
    // publish the service in the discovery infrastructure
    publishEventBusService(SettlementService.SERVICE_NAME, SettlementService.SERVICE_ADDRESS, SettlementService.class)
      .compose(servicePublished ->
        publishMessageSource("shopping-payment-message-source", SettlementService.PAYMENT_EVENT_ADDRESS))
      .compose(servicePublished ->
        publishMessageSource("shopping-order-message-source", SettlementService.ORDER_EVENT_ADDRESS))
      .setHandler(future.completer());
  }
}

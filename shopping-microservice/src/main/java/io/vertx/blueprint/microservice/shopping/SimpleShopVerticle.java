package io.vertx.blueprint.microservice.shopping;

import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.core.Future;
import io.vertx.serviceproxy.ProxyHelper;

import static io.vertx.blueprint.microservice.shopping.ShopService.SERVICE_ADDRESS;
import static io.vertx.blueprint.microservice.shopping.ShopService.SERVICE_NAME;

/**
 * A simple verticle for shopping service.
 *
 * @author Eric Zhao
 */
public class SimpleShopVerticle extends BaseMicroserviceVerticle {

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();

    // create the service instance
    ShopService shopService = ShopService.createService(vertx, discovery);
    // register the service proxy on event bus
    ProxyHelper.registerService(ShopService.class, vertx, shopService, SERVICE_ADDRESS);
    // publish the service in the discovery infrastructure
    publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, ShopService.class)
      .setHandler(future.completer());
  }
}

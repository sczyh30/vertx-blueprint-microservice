package io.vertx.blueprint.microservice.cache;

import io.vertx.blueprint.microservice.cache.impl.DefaultCounterServiceImpl;
import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.core.Future;
import io.vertx.serviceproxy.ProxyHelper;

/**
 * A verticle provides cache and counter functionality.
 *
 * @author Eric Zhao
 */
public class CacheComponentVerticle extends BaseMicroserviceVerticle {

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();

    // create the service instance
    CounterService counterService = new DefaultCounterServiceImpl(vertx, config());
    // register the service proxy on event bus
    ProxyHelper.registerService(CounterService.class, vertx, counterService, CounterService.SERVICE_ADDRESS);
    // publish the service in the discovery infrastructure
    publishEventBusService(CounterService.SERVICE_NAME, CounterService.SERVICE_ADDRESS, CounterService.class)
      .setHandler(future.completer());
  }
}

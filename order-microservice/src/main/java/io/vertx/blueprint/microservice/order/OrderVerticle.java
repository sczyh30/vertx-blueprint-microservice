package io.vertx.blueprint.microservice.order;

import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.blueprint.microservice.order.api.RestOrderAPIVerticle;
import io.vertx.blueprint.microservice.order.impl.OrderServiceImpl;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.serviceproxy.ProxyHelper;

import static io.vertx.blueprint.microservice.order.OrderService.*;

/**
 * A verticle deploys multiple verticles for order operation and dispatching.
 *
 * @author Eric Zhao
 */
public class OrderVerticle extends BaseMicroserviceVerticle {

  private OrderService orderService;

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();
    this.orderService = new OrderServiceImpl(vertx, config());
    ProxyHelper.registerService(OrderService.class, vertx, orderService, SERVICE_ADDRESS);

    initOrderDatabase()
      .compose(databaseOkay -> publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, OrderService.class))
      .compose(servicePublished -> prepareDispatcher())
      .compose(dispatcherPrepared -> deployRestVerticle())
      .setHandler(future.completer());
  }

  private Future<Void> initOrderDatabase() {
    Future<Void> initFuture = Future.future();
    orderService.initializePersistence(initFuture.completer());
    return initFuture;
  }

  private Future<Void> prepareDispatcher() {
    Future<String> future = Future.future();
    vertx.deployVerticle(new RawOrderDispatcher(orderService),
      new DeploymentOptions().setConfig(config()),
      future.completer());
    return future.map(r -> null);
  }

  private Future<Void> deployRestVerticle() {
    Future<String> future = Future.future();
    vertx.deployVerticle(new RestOrderAPIVerticle(orderService),
      new DeploymentOptions().setConfig(config()),
      future.completer());
    return future.map(r -> null);
  }
}

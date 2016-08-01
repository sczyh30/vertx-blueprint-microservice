package io.vertx.blueprint.microservice.product.impl;

import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.blueprint.microservice.product.ProductService;
import io.vertx.core.Future;
import io.vertx.serviceproxy.ProxyHelper;

import static io.vertx.blueprint.microservice.product.ProductService.SERVICE_ADDRESS;


/**
 * A verticle publishing the product service.
 *
 * @author Eric Zhao
 */
public class ProductVerticle extends BaseMicroserviceVerticle {

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();

    // create the service instance
    ProductService userService = ProductService.createService(vertx, config());
    // register the service proxy on event bus
    ProxyHelper.registerService(ProductService.class, vertx, userService, SERVICE_ADDRESS);
    // publish the service in the discovery infrastructure
    publishEventBusService(ProductService.SERVICE_NAME, SERVICE_ADDRESS, ProductService.class)
      .compose(servicePublished -> publishJDBCDataSource("product-jdbc-data-source", config()))
      .compose(sourcePublished -> deployRestService())
      .setHandler(future.completer());
  }

  private Future<Void> deployRestService() {
    Future<Void> future = Future.future();
    vertx.deployVerticle(new RestProductAPIVerticle(), ar -> {
      if (ar.succeeded()) {
        future.complete();
      } else {
        future.fail(ar.cause());
      }
    });
    return future;
  }

}

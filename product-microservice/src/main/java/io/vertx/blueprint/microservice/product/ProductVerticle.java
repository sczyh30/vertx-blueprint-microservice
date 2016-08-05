package io.vertx.blueprint.microservice.product;

import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.blueprint.microservice.product.api.RestProductAPIVerticle;
import io.vertx.blueprint.microservice.product.impl.ProductServiceImpl;
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
    ProductService productService = new ProductServiceImpl(vertx, config());
    // register the service proxy on event bus
    ProxyHelper.registerService(ProductService.class, vertx, productService, SERVICE_ADDRESS);
    // publish the service in the discovery infrastructure
    publishEventBusService(ProductService.SERVICE_NAME, SERVICE_ADDRESS, ProductService.class)
      .compose(servicePublished -> publishJDBCDataSource("product-jdbc-data-source", config()))
      .compose(sourcePublished -> deployRestService(productService))
      .setHandler(future.completer());
  }

  private Future<Void> deployRestService(ProductService service) {
    Future<Void> future = Future.future();
    vertx.deployVerticle(new RestProductAPIVerticle(service), ar -> {
      if (ar.succeeded()) {
        future.complete();
      } else {
        future.fail(ar.cause());
      }
    });
    return future;
  }

}

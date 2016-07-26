package io.vertx.blueprint.microservice.product.impl;

import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.blueprint.microservice.product.ProductService;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.serviceproxy.ProxyHelper;

import static io.vertx.blueprint.microservice.product.ProductService.ADDRESS;


/**
 * A verticle publishing the product service.
 *
 * @author Eric Zhao
 */
public class ProductVerticle extends BaseMicroserviceVerticle {

  private static final Logger logger = LoggerFactory.getLogger(ProductVerticle.class);

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();

    // create the service instance
    ProductService userService = ProductService.createService(vertx, config());
    // register the service proxy on event bus
    ProxyHelper.registerService(ProductService.class, vertx, userService, ADDRESS);
    // publish the service in the discovery infrastructure
    publishEventBusService("product-eb", ADDRESS, ProductService.class, ar -> {
      if (ar.failed()) {
        future.fail(ar.cause());
      } else {
        logger.info("Product service published");
        // we also publish jdbc source in the discovery infrastructure
        publishJDBCDataSource("product-jdbc-data-source-service", config(), ar1 -> {
          if (ar1.failed()) {
            future.fail(ar1.cause());
          } else {
            future.complete();
            logger.info("Product JDBC data source service published");
          }
        });
      }
    });
  }

}

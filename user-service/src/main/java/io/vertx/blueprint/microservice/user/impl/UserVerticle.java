package io.vertx.blueprint.microservice.user.impl;

import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.blueprint.microservice.user.UserService;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.serviceproxy.ProxyHelper;

import static io.vertx.blueprint.microservice.user.UserService.ADDRESS;

/**
 * A verticle publishing the user service.
 *
 * @author Eric Zhao
 */
public class UserVerticle extends BaseMicroserviceVerticle {

  private static final Logger logger = LoggerFactory.getLogger(UserVerticle.class);

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();

    // create the service instance
    UserService userService = UserService.createService(vertx, config());
    // register the service proxy on event bus
    ProxyHelper.registerService(UserService.class, vertx, userService, ADDRESS);
    // publish the service in the discovery infrastructure
    publishEventBusService("user-eb", ADDRESS, UserService.class, ar -> {
      if (ar.failed()) {
        future.fail(ar.cause());
      } else {
        logger.info("User service published");
        // we also publish jdbc source in the discovery infrastructure
        publishJDBCDataSource("user-jdbc-data-source-service", config(), ar1 -> {
          if (ar1.failed()) {
            future.fail(ar1.cause());
          } else {
            future.complete();
            logger.info("User JDBC data source service published");
          }
        });
      }
    });

  }


}

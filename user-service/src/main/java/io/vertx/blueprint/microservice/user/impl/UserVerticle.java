package io.vertx.blueprint.microservice.user.impl;

import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.blueprint.microservice.user.UserService;
import io.vertx.core.Future;
import io.vertx.serviceproxy.ProxyHelper;

import static io.vertx.blueprint.microservice.user.UserService.SERVICE_ADDRESS;
import static io.vertx.blueprint.microservice.user.UserService.SERVICE_NAME;


/**
 * A verticle publishing the user service.
 *
 * @author Eric Zhao
 */
public class UserVerticle extends BaseMicroserviceVerticle {

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();

    // create the service instance
    UserService userService = UserService.createService(vertx, config());
    // register the service proxy on event bus
    ProxyHelper.registerService(UserService.class, vertx, userService, SERVICE_ADDRESS);
    // publish the service and JDBC data source in the discovery infrastructure
    publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, UserService.class)
      .compose(servicePublished -> publishJDBCDataSource("user-jdbc-data-source-service", config()))
      .setHandler(future.completer());
  }
}

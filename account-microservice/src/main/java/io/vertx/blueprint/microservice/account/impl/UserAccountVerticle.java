package io.vertx.blueprint.microservice.account.impl;

import io.vertx.blueprint.microservice.account.AccountService;
import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.core.Future;
import io.vertx.serviceproxy.ProxyHelper;

import static io.vertx.blueprint.microservice.account.AccountService.SERVICE_ADDRESS;
import static io.vertx.blueprint.microservice.account.AccountService.SERVICE_NAME;


/**
 * A verticle publishing the user service.
 *
 * @author Eric Zhao
 */
public class UserAccountVerticle extends BaseMicroserviceVerticle {

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();

    // create the service instance
    AccountService accountService = new JdbcAccountServiceImpl(vertx, config());
    // register the service proxy on event bus
    ProxyHelper.registerService(AccountService.class, vertx, accountService, SERVICE_ADDRESS);
    // publish the service and JDBC data source in the discovery infrastructure
    publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, AccountService.class)
      .compose(servicePublished -> publishJDBCDataSource("user-jdbc-data-source-service", config()))
      .setHandler(future.completer());
  }
}

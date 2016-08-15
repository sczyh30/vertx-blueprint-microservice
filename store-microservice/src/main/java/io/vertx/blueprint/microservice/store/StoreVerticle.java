package io.vertx.blueprint.microservice.store;

import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.blueprint.microservice.store.api.RestStoreAPIVerticle;
import io.vertx.blueprint.microservice.store.impl.StoreCRUDServiceImpl;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.serviceproxy.ProxyHelper;

import static io.vertx.blueprint.microservice.store.StoreCRUDService.SERVICE_ADDRESS;
import static io.vertx.blueprint.microservice.store.StoreCRUDService.SERVICE_NAME;

/**
 * A verticle for store operation (e.g. apply or close) processing.
 *
 * @author Eric Zhao
 */
public class StoreVerticle extends BaseMicroserviceVerticle {

  private StoreCRUDService crudService;

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();

    crudService = new StoreCRUDServiceImpl(vertx, config());
    ProxyHelper.registerService(StoreCRUDService.class, vertx, crudService, SERVICE_ADDRESS);
    // publish service and deploy REST verticle
    publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, StoreCRUDService.class)
      .compose(servicePublished -> deployRestVerticle(crudService))
      .setHandler(future.completer());
  }

  private Future<Void> deployRestVerticle(StoreCRUDService service) {
    Future<String> future = Future.future();
    vertx.deployVerticle(new RestStoreAPIVerticle(service),
      new DeploymentOptions().setConfig(config()),
      future.completer());
    return future.map(r -> null);
  }
}

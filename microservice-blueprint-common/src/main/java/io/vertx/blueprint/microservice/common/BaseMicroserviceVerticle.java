package io.vertx.blueprint.microservice.common;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.docker.DockerLinksServiceImporter;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.servicediscovery.types.JDBCDataSource;
import io.vertx.servicediscovery.types.MessageSource;


/**
 * This verticle provides support for service discovery.
 *
 * @author Eric Zhao
 */
public abstract class BaseMicroserviceVerticle extends AbstractVerticle {

  protected ServiceDiscovery discovery;

  @Override
  public void start() throws Exception {
    discovery = ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions().setBackendConfiguration(config()));
    discovery.registerServiceImporter(new DockerLinksServiceImporter(), new JsonObject());
  }

  protected void publishMessageSource(String name, String address, Handler<AsyncResult<Void>> completionHandler) {
    Record record = MessageSource.createRecord(name, address);
    publish(completionHandler, record);
  }

  protected void publishJDBCDataSource(String name, JsonObject location, Handler<AsyncResult<Void>> completionHandler) {
    Record record = JDBCDataSource.createRecord(name, location, new JsonObject());
    publish(completionHandler, record);
  }

  protected void publishEventBusService(String name, String address, Class serviceClass,
                                        Handler<AsyncResult<Void>> completionHandler) {
    Record record = EventBusService.createRecord(name, address, serviceClass);
    publish(completionHandler, record);
  }

  private void publish(Handler<AsyncResult<Void>> completionHandler, Record record) {
    if (discovery == null) {
      try {
        start();
      } catch (Exception e) {
        throw new IllegalStateException("Cannot create discovery service");
      }
    }

    discovery.publish(record, ar -> {
      if (ar.succeeded()) {
        completionHandler.handle(Future.succeededFuture());
      } else {
        completionHandler.handle(Future.failedFuture(ar.cause()));
      }
    });
  }

  @Override
  public void stop(Future<Void> future) throws Exception {
    discovery.close();
    future.complete();
  }
}

package io.vertx.blueprint.microservice.common;

import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.docker.DockerLinksServiceImporter;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.servicediscovery.types.HttpEndpoint;
import io.vertx.servicediscovery.types.JDBCDataSource;
import io.vertx.servicediscovery.types.MessageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * This verticle provides support for service discovery.
 *
 * @author Eric Zhao
 */
public abstract class BaseMicroserviceVerticle extends AbstractVerticle {

  private static final String LOG_EVENT_ADDRESS = "events.log";

  private static final Logger logger = LoggerFactory.getLogger(BaseMicroserviceVerticle.class);

  protected ServiceDiscovery discovery;
  protected CircuitBreaker circuitBreaker;
  protected Set<Record> registeredRecords = new ConcurrentHashSet<>();

  // Metrics field
  protected long deployedAt;

  @Override
  public void start() throws Exception {
    // init service discovery instance
    discovery = ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions().setBackendConfiguration(config()));
    discovery.registerServiceImporter(new DockerLinksServiceImporter(), new JsonObject());

    // init circuit breaker instance
    JsonObject cbOptions = config().getJsonObject("circuit-breaker") != null ?
      config().getJsonObject("circuit-breaker") : new JsonObject();
    circuitBreaker = CircuitBreaker.create(cbOptions.getString("name", "circuit-breaker"), vertx,
      new CircuitBreakerOptions()
        .setMaxFailures(cbOptions.getInteger("maxFailures", 5))
        .setTimeout(cbOptions.getLong("timeout", 10000L))
        .setFallbackOnFailure(true)
        .setResetTimeout(cbOptions.getLong("resetTimeout", 30000L))
    );

    this.deployedAt = System.currentTimeMillis();
  }

  protected Future<Void> publishHttpEndpoint(String name, String host, int port) {
    Record record = HttpEndpoint.createRecord(name, host, port, "/");
    return publish(record);
  }

  protected Future<Void> publishMessageSource(String name, String address) {
    Record record = MessageSource.createRecord(name, address);
    return publish(record);
  }

  protected Future<Void> publishJDBCDataSource(String name, JsonObject location) {
    Record record = JDBCDataSource.createRecord(name, location, new JsonObject());
    return publish(record);
  }

  protected Future<Void> publishEventBusService(String name, String address, Class serviceClass) {
    Record record = EventBusService.createRecord(name, address, serviceClass);
    return publish(record);
  }

  /**
   * Publish a service with record.
   *
   * @param record service record
   * @return async result
   */
  private Future<Void> publish(Record record) {
    if (discovery == null) {
      try {
        start();
      } catch (Exception e) {
        throw new IllegalStateException("Cannot create discovery service");
      }
    }

    Future<Void> future = Future.future();
    // publish the service
    discovery.publish(record, ar -> {
      if (ar.succeeded()) {
        registeredRecords.add(record);
        logger.info("Service <" + ar.result().getName() + "> published");
        future.complete();
      } else {
        future.fail(ar.cause());
      }
    });

    return future;
  }

  /**
   * A helper method that simply publish logs on the event bus
   *
   * @param type log type
   * @param data log message data
   */
  protected void publishLogEvent(String type, JsonObject data) {
    JsonObject msg = new JsonObject().put("type", type)
      .put("message", data);
    vertx.eventBus().publish(LOG_EVENT_ADDRESS, msg);
  }

  protected void publishLogEvent(String type, JsonObject data, boolean succeeded) {
    JsonObject msg = new JsonObject().put("type", type)
      .put("status", succeeded ? 1 : 0)
      .put("message", data);
    vertx.eventBus().publish(LOG_EVENT_ADDRESS, msg);
  }

  @Override
  public void stop(Future<Void> future) throws Exception {
    // In current design, the publisher is responsible for removing the service
    List<Future> futures = new ArrayList<>();
    for (Record record : registeredRecords) {
      Future<Void> unregistrationFuture = Future.future();
      futures.add(unregistrationFuture);
      discovery.unpublish(record.getRegistration(), unregistrationFuture.completer());
    }

    if (futures.isEmpty()) {
      discovery.close();
      future.complete();
    } else {
      CompositeFuture.all(futures)
        .setHandler(ar -> {
          discovery.close();
          if (ar.failed()) {
            future.fail(ar.cause());
          } else {
            future.complete();
          }
        });
    }
  }
}

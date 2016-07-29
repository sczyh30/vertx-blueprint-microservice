package io.vertx.blueprint.microservice.cache;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * A service interface for global cache and counter management using a cache backend (e.g. Redis).
 * <p>
 * This service is an event bus service (aka. service proxy)
 * </p>
 *
 * @author Eric Zhao
 */
@VertxGen
@ProxyGen
public interface CounterService {

  /**
   * The name of the event bus service.
   */
  String SERVICE_NAME = "counter-eb-service";

  /**
   * The address on which the service is published.
   */
  String SERVICE_ADDRESS = "service.counter";

  /**
   * First add the counter, then retrieve.
   *
   * @param key           counter key
   * @param resultHandler async result handler
   */
  void addThenRetrieve(String key, Handler<AsyncResult<Long>> resultHandler);

  /**
   * First add the counter by a {@code increment}, then retrieve.
   *
   * @param key           counter key
   * @param increment     increment step
   * @param resultHandler async result handler
   */
  void addThenRetrieveBy(String key, Long increment, Handler<AsyncResult<Long>> resultHandler);

  /**
   * First retrieve the counter, then add.
   *
   * @param key           counter key
   * @param resultHandler async result handler
   */
  void retrieveThenAdd(String key, Handler<AsyncResult<Long>> resultHandler);

  /**
   * Reset the value of the counter with a certain {@code key}
   *
   * @param key           counter key
   * @param resultHandler async result handler
   */
  void reset(String key, Handler<AsyncResult<Void>> resultHandler);

}

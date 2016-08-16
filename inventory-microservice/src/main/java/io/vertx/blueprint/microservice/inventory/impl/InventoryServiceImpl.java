package io.vertx.blueprint.microservice.inventory.impl;

import io.vertx.blueprint.microservice.inventory.InventoryService;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

/**
 * Implementation of {@link InventoryService}.
 */
public class InventoryServiceImpl implements InventoryService {

  private static final String PREFIX = "inventory:v1:"; // version I, very simple

  private final RedisClient client;

  public InventoryServiceImpl(Vertx vertx, JsonObject config) {
    RedisOptions redisOptions = new RedisOptions()
      .setHost(config.getString("redis.host", "localhost"))
      .setPort(config.getInteger("redis.port", 6379));
    this.client = RedisClient.create(vertx, redisOptions);
  }

  @Override
  public Future<Integer> increase(String productId, int increase) {
    Future<Long> future = Future.future();
    client.incrby(PREFIX + productId, increase, future.completer());
    return future.map(Long::intValue);
  }

  @Override
  public Future<Integer> decrease(String productId, int decrease) {
    Future<Long> future = Future.future();
    client.decrby(PREFIX + productId, decrease, future.completer());
    return future.map(Long::intValue);
  }

  @Override
  public Future<Integer> retrieveInventoryForProduct(String productId) {
    Future<String> future = Future.future();
    client.get(PREFIX + productId, future.completer());
    return future.map(r -> r == null ? "0" : r)
      .map(Integer::valueOf);
  }

}

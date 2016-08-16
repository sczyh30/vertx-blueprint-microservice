package io.vertx.blueprint.microservice.cache.impl;

import io.vertx.blueprint.microservice.cache.CounterService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

/**
 * Redis implementation of {@link CounterService}
 */
public class DefaultCounterServiceImpl implements CounterService {

  private static final String COUNTER_PREFIX = "counter:";

  private RedisClient client;

  public DefaultCounterServiceImpl(Vertx vertx, JsonObject config) {
    RedisOptions redisOptions = new RedisOptions()
      .setHost(config.getString("redis.host", "localhost"))
      .setPort(config.getInteger("redis.port", 6379));
    this.client = RedisClient.create(vertx, redisOptions);
  }

  @Override
  public void addThenRetrieve(String key, Handler<AsyncResult<Long>> resultHandler) {
    client.incr(COUNTER_PREFIX + key, ar -> {
      if (ar.succeeded()) {
        resultHandler.handle(Future.succeededFuture(ar.result()));
      } else {
        resultHandler.handle(Future.failedFuture(ar.cause()));
      }
    });
  }

  @Override
  public void addThenRetrieveBy(String key, Long increment, Handler<AsyncResult<Long>> resultHandler) {
    client.incrby(COUNTER_PREFIX + key, increment, ar -> {
      if (ar.succeeded()) {
        resultHandler.handle(Future.succeededFuture(ar.result()));
      } else {
        resultHandler.handle(Future.failedFuture(ar.cause()));
      }
    });
  }

  @Override
  public void retrieveThenAdd(String key, Handler<AsyncResult<Long>> resultHandler) {
    client.incr(COUNTER_PREFIX + key, ar -> {
      if (ar.succeeded()) {
        resultHandler.handle(Future.succeededFuture(ar.result() - 1));
      } else {
        resultHandler.handle(Future.failedFuture(ar.cause()));
      }
    });
  }

  @Override
  public void reset(String key, Handler<AsyncResult<Void>> resultHandler) {
    client.set(COUNTER_PREFIX + key, "0", ar -> {
      if (ar.succeeded()) {
        resultHandler.handle(Future.succeededFuture());
      } else {
        resultHandler.handle(Future.failedFuture(ar.cause()));
      }
    });
  }
}

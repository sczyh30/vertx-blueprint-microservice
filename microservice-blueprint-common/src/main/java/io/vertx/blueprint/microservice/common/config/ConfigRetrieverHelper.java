package io.vertx.blueprint.microservice.common.config;

import java.util.Objects;
import java.util.Optional;

import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.config.ConfigRetriever;
import rx.Observable;

/**
 * Helper class for Vert.x configuration retriever.
 */
public enum ConfigRetrieverHelper {
  configurationRetriever;

  private static final Logger logger = LoggerFactory.getLogger(ConfigRetrieverHelper.class);

  private ConfigRetriever configRetriever;
  private ConfigRetrieverOptions options = new ConfigRetrieverOptions();

  public ConfigRetrieverHelper usingScanPeriod(final long scanPeriod) {
    options.setScanPeriod(scanPeriod);
    return this;
  }

  public Observable<JsonObject> rxCreateConfig(final Vertx vertx) {
    configRetriever = ConfigRetriever.create(io.vertx.rxjava.core.Vertx.newInstance(vertx), options);

    // TODO: improve here.
    Observable<JsonObject> configObservable = Observable.create(subscriber -> {
      configRetriever.getConfig(ar -> {
        if (ar.failed()) {
          logger.info("Failed to retrieve configuration");
        } else {
          final JsonObject config =
            vertx.getOrCreateContext().config().mergeIn(
              Optional.ofNullable(ar.result()).orElse(new JsonObject()));
          subscriber.onNext(config);
        }
      });

      configRetriever.listen(ar -> {
        final JsonObject config =
          vertx.getOrCreateContext().config().mergeIn(
            Optional.ofNullable(ar.getNewConfiguration()).orElse(new JsonObject()));
        subscriber.onNext(config);
      });
    });

    configObservable.onErrorReturn(t -> {
      logger.error("Failed to emit configuration - Returning null", t);
      return null;
    });

    return configObservable.filter(Objects::nonNull);
  }

  public ConfigRetrieverHelper withHttpStore(final String host, final int port, final String path) {
    ConfigStoreOptions httpStore = new ConfigStoreOptions()
      .setType("http")
      .setConfig(new JsonObject()
        .put("host", host).put("port", port).put("path", path));

    options.addStore(httpStore);
    return this;
  }
}
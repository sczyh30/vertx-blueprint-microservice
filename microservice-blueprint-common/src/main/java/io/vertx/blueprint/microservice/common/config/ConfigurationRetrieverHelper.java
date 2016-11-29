package io.vertx.blueprint.microservice.common.config;

import java.util.Objects;
import java.util.Optional;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.configuration.ConfigurationRetriever;
import io.vertx.ext.configuration.ConfigurationRetrieverOptions;
import io.vertx.ext.configuration.ConfigurationStoreOptions;
import rx.Observable;

/**
 * Helper class for configuration retriever.
 */
public enum ConfigurationRetrieverHelper {
  configurationRetriever;

  private static final Logger logger = LoggerFactory.getLogger(ConfigurationRetrieverHelper.class);

  private ConfigurationRetriever confRetriever;
  private ConfigurationRetrieverOptions options = new ConfigurationRetrieverOptions();

  public ConfigurationRetrieverHelper usingScanPeriod(final long scanPeriod) {
    options.setScanPeriod(scanPeriod);
    return this;
  }

  public Observable<JsonObject> createConfigObservable(final Vertx vertx) {
    confRetriever = ConfigurationRetriever.create(vertx, options);

    final Observable<JsonObject> configObservable = Observable.create(subscriber -> {
      confRetriever.getConfiguration(ar -> {
        if (ar.failed()) {
          logger.info("Failed to retrieve configuration");
        } else {
          final JsonObject config =
            vertx.getOrCreateContext().config().mergeIn(
              Optional.ofNullable(ar.result()).orElse(new JsonObject()));
          subscriber.onNext(config);
        }
      });

      confRetriever.listen(ar -> {
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

  public ConfigurationRetrieverHelper withHttpStore(final String host, final int port, final String path) {
    ConfigurationStoreOptions httpStore = new ConfigurationStoreOptions()
      .setType("http")
      .setConfig(new JsonObject()
        .put("host", host).put("port", port).put("path", path));

    options.addStore(httpStore);
    return this;
  }
}
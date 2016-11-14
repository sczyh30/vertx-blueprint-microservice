package io.vertx.blueprint.microservice.common.config;

import java.util.Objects;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.configuration.ConfigurationService;
import io.vertx.ext.configuration.ConfigurationServiceOptions;
import io.vertx.ext.configuration.ConfigurationStoreOptions;
import rx.Observable;

import static io.vertx.ext.configuration.ConfigurationService.create;
import static java.util.Optional.ofNullable;

public enum ConfigurationServiceHelper {
  configurationService;

  private static final Logger logger = LoggerFactory.getLogger(ConfigurationServiceHelper.class);

  private ConfigurationService confSvc;
  private ConfigurationServiceOptions options = new ConfigurationServiceOptions();

  public ConfigurationServiceHelper usingScanPeriod(final long scanPeriod) {
    options.setScanPeriod(scanPeriod);
    return this;
  }

  public Observable<JsonObject> createConfigObservable(final Vertx vertx) {
    confSvc = create(vertx, options);

    final Observable<JsonObject> configObservable = Observable.create(subscriber -> {
      confSvc.getConfiguration(ar -> {
        if (ar.failed()) {
          logger.info("Failed to retrieve configuration");
        } else {
          final JsonObject config =
            vertx.getOrCreateContext().config().mergeIn(
              ofNullable(ar.result()).orElse(new JsonObject()));
          subscriber.onNext(config);
        }
      });

      confSvc.listen(ar -> {
        final JsonObject config =
          vertx.getOrCreateContext().config().mergeIn(
            ofNullable(ar.getNewConfiguration()).orElse(new JsonObject()));
        subscriber.onNext(config);
      });
    });

    configObservable.onErrorReturn(t -> {
      logger.error("Failed to emit configuration - Returning null", t);
      return null;
    });

    return configObservable.filter(Objects::nonNull);
  }

  public ConfigurationServiceHelper withHttpStore(final String host, final int port, final String path) {
    ConfigurationStoreOptions httpStore = new ConfigurationStoreOptions()
      .setType("http")
      .setConfig(new JsonObject()
        .put("host", host).put("port", port).put("path", path));

    options.addStore(httpStore);
    return this;
  }
}
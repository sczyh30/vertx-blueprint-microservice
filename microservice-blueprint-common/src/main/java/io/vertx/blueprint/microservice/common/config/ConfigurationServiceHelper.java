package io.vertx.blueprint.microservice.common.config;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.configuration.ConfigurationService;
import io.vertx.ext.configuration.ConfigurationServiceOptions;
import io.vertx.ext.configuration.ConfigurationStoreOptions;

import static io.vertx.ext.configuration.ConfigurationService.create;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

public enum ConfigurationServiceHelper {
  configurationService;

  private static final Logger logger = LoggerFactory.getLogger(ConfigurationServiceHelper.class);

  private ConfigurationService confSvc;
  private ConfigurationServiceOptions options = new ConfigurationServiceOptions();
  private List<ConfigurationServiceInitHandler> initializers = new ArrayList<>();
  private List<ConfigurationServiceUpdateHandler> updaters = new ArrayList<>();

  public ConfigurationServiceHelper usingScanPeriod(final long scanPeriod) {
    options.setScanPeriod(scanPeriod);
    return this;
  }

  public ConfigurationServiceHelper start(final Vertx vertx, final Context context) {
    confSvc = create(vertx, options);

    confSvc.getConfiguration(ar -> {
      if (ar.failed()) {
        logger.info("Failed to retrieve configuration...");
      } else {
        final JsonObject config =
          context.config().mergeIn(ofNullable(ar.result()).orElse(new JsonObject()));
        initializers.forEach(initializer -> initializer.initialize(config));
      }
    });

    confSvc.listen(ar -> {
      final JsonObject config =
        context.config().mergeIn(ofNullable(ar.getNewConfiguration()).orElse(new JsonObject()));
      updaters.forEach(updater -> updater.update(config));
    });
    return this;
  }

  public ConfigurationServiceHelper withHttpStore(final String host, final int port, final String path) {
    ConfigurationStoreOptions httpStore = new ConfigurationStoreOptions()
      .setType("http")
      .setConfig(new JsonObject()
        .put("host", host).put("port", port).put("path", path));

    options.addStore(httpStore);
    return this;
  }

  public ConfigurationServiceHelper withInitializer(final ConfigurationServiceInitHandler initializer) {
    requireNonNull(initializer);
    initializers.add(initializer);
    return this;
  }

  public ConfigurationServiceHelper withUpdater(final ConfigurationServiceUpdateHandler updater) {
    requireNonNull(updater);
    updaters.add(updater);
    return this;
  }

  public ConfigurationServiceHelper withHandlers(final ConfigurationServiceInitHandler initializer,
                                                 final ConfigurationServiceUpdateHandler updater) {
    withInitializer(initializer);
    return withUpdater(updater);
  }
}
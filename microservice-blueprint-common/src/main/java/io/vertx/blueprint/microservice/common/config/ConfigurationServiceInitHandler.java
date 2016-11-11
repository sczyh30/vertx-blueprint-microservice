package io.vertx.blueprint.microservice.common.config;

import io.vertx.core.json.JsonObject;

@FunctionalInterface
public interface ConfigurationServiceInitHandler {
  void initialize(final JsonObject config);
}
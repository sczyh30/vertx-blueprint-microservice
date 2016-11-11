package io.vertx.blueprint.microservice.common.config;

import io.vertx.core.json.JsonObject;

@FunctionalInterface
public interface ConfigurationServiceUpdateHandler {
  void update(final JsonObject config);
}
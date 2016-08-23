package io.vertx.blueprint.microservice.order;

import io.vertx.codegen.annotations.VertxGen;

/**
 * Order event type.
 */
@VertxGen
public enum OrderEventType {
  CREATED,
  PAID,
  SHIPPED,
  DELIVERED
}

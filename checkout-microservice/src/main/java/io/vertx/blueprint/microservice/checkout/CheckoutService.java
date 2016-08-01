package io.vertx.blueprint.microservice.checkout;

import io.vertx.blueprint.microservice.common.entity.ProductTuple;
import io.vertx.blueprint.microservice.checkout.impl.CheckoutServiceImpl;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceDiscovery;

import java.util.List;

/**
 * A service interface for shopping checkout (order transaction submission) logic.
 * <p>
 * This service is an event bus service (aka. service proxy).
 * </p>
 *
 * @author Eric Zhao
 */
@VertxGen
@ProxyGen
public interface CheckoutService {

  /**
   * The name of the event bus service.
   */
  String SERVICE_NAME = "shopping-checkout-eb-service";

  /**
   * The address on which the service is published.
   */
  String SERVICE_ADDRESS = "service.shopping.checkout";

  /**
   * Payment and order event address.
   */
  String PAYMENT_EVENT_ADDRESS = "events.service.shopping.to.payment";
  String ORDER_EVENT_ADDRESS = "events.service.shopping.to.order";

  /**
   * Create a shopping checkout service instance
   */
  static CheckoutService createService(Vertx vertx, ServiceDiscovery discovery) {
    return new CheckoutServiceImpl(vertx, discovery);
  }

  /**
   * Buy some things :-)
   *
   * @param userId   User id
   * @param products The products the user wants to buy
   * @param handler  Async result handler
   */
  @Fluent
  CheckoutService buy(String userId, List<ProductTuple> products, Handler<AsyncResult<JsonObject>> handler);

}

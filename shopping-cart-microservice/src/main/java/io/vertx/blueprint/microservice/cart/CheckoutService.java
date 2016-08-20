package io.vertx.blueprint.microservice.cart;

import io.vertx.blueprint.microservice.cart.impl.CheckoutServiceImpl;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.servicediscovery.ServiceDiscovery;


/**
 * A service interface for shopping cart checkout logic.
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
  String SERVICE_ADDRESS = "service.shopping.cart.checkout";

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
   * Shopping cart checkout.
   *
   * @param userId  user id
   * @param handler async result handler
   */
  void checkout(String userId, Handler<AsyncResult<CheckoutResult>> handler);

}

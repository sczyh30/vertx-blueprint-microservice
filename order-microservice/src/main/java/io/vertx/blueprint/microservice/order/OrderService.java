package io.vertx.blueprint.microservice.order;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;

/**
 * A service interface managing order storage operations.
 * <p>
 * This service is an event bus service (aka. service proxy).
 * </p>
 *
 * @author Eric Zhao
 */
@VertxGen
@ProxyGen
public interface OrderService {

  /**
   * The name of the event bus service.
   */
  String SERVICE_NAME = "order-storage-eb-service";

  /**
   * The address on which the service is published.
   */
  String SERVICE_ADDRESS = "service.order.storage";

  /**
   * Initialize the persistence.
   *
   * @param resultHandler async result handler
   */
  @Fluent
  OrderService initializePersistence(Handler<AsyncResult<Void>> resultHandler);

  /**
   * Retrieve orders belonging to a certain account.
   *
   * @param accountId     account id
   * @param resultHandler async result handler
   */
  @Fluent
  OrderService retrieveOrdersForAccount(String accountId, Handler<AsyncResult<List<Order>>> resultHandler);

  /**
   * Save an order into the persistence.
   *
   * @param order         order data object
   * @param resultHandler async result handler
   */
  @Fluent
  OrderService createOrder(Order order, Handler<AsyncResult<Void>> resultHandler);

  /**
   * Retrieve the order with a certain {@code orderId}.
   *
   * @param orderId       order id
   * @param resultHandler async result handler
   */
  @Fluent
  OrderService retrieveOrder(Long orderId, Handler<AsyncResult<Order>> resultHandler);

}

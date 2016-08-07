package io.vertx.blueprint.microservice.order;

import io.vertx.blueprint.microservice.cart.CheckoutResult;
import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.types.MessageSource;

/**
 * A verticle for raw order wrapping and dispatching.
 *
 * @author Eric Zhao
 */
public class RawOrderDispatcher extends BaseMicroserviceVerticle {

  private final OrderService orderService;

  public RawOrderDispatcher(OrderService orderService) {
    this.orderService = orderService;
  }

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();
    MessageSource.<JsonObject>getConsumer(discovery,
      new JsonObject().put("name", "shopping-order-message-source"),
      ar -> {
        if (ar.succeeded()) {
          MessageConsumer<JsonObject> orderConsumer = ar.result();
          orderConsumer.handler(message -> {
            Order wrappedOrder = wrapRawOrder(message.body());
            dispatchOrder(wrappedOrder, message);
          });
          future.complete();
        } else {
          future.fail(ar.cause());
        }
      });
  }

  /**
   * Wrap raw order and generate new order.
   *
   * @return the new order.
   */
  private Order wrapRawOrder(JsonObject rawOrder) {
    return new Order(rawOrder)
      .setCreateTime(System.currentTimeMillis());
  }

  /**
   * Dispatch the order to the infrastructure layer.
   * Here we simply save the order to the persistence.
   *
   * @param order  order data object
   * @param sender message sender
   */
  private void dispatchOrder(Order order, Message<JsonObject> sender) {
    orderService.createOrder(order, ar -> {
      if (ar.succeeded()) {
        CheckoutResult result = new CheckoutResult("checkout_success", order);
        sender.reply(result.toJson());
        publishLogEvent("checkout", result.toJson(), true);
      } else {
        sender.fail(5000, ar.cause().getMessage());
        ar.cause().printStackTrace();
      }
    });
  }
}

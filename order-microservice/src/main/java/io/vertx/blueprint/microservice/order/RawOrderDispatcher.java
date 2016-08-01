package io.vertx.blueprint.microservice.order;

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

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();
    receiveAndDispatch().setHandler(future.completer());
  }

  /**
   * Receive raw order data from the message source and then dispatch the order.
   *
   * @return async result of the procedure
   */
  private Future<Void> receiveAndDispatch() {
    Future<Void> future = Future.future();
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
    return future;
  }

  private Order wrapRawOrder(JsonObject rawOrder) {
    return new Order(rawOrder)
      .setCreateTime(System.currentTimeMillis());
  }

  private void dispatchOrder(Order order, Message<JsonObject> sender) {

  }
}

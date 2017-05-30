package io.vertx.blueprint.microservice.order;

import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.HttpEndpoint;
import io.vertx.servicediscovery.types.MessageSource;

import java.util.List;
import java.util.stream.Collectors;

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
   * Here we simply save the order to the persistence and modify inventory changes.
   *
   * @param order  order data object
   * @param sender message sender
   */
  private void dispatchOrder(Order order, Message<JsonObject> sender) {
    Future<Void> orderCreateFuture = Future.future();
    orderService.createOrder(order, orderCreateFuture.completer());
    orderCreateFuture
      .compose(orderCreated -> applyInventoryChanges(order))
      .setHandler(ar -> {
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

  /**
   * Apply inventory decrease changes according to the order.
   *
   * @param order order data object
   * @return async result
   */
  private Future<Void> applyInventoryChanges(Order order) {
    Future<Void> future = Future.future();
    // get REST endpoint
    Future<HttpClient> clientFuture = Future.future();
    HttpEndpoint.getClient(discovery,
      new JsonObject().put("name", "inventory-rest-api"),
      clientFuture.completer());
    // modify the inventory changes via REST API
    return clientFuture.compose(client -> {
      List<Future> futures = order.getProducts()
        .stream()
        .map(item -> {
          Future<Void> resultFuture = Future.future();
          String url = String.format("/%s/decrease?n=%d", item.getProductId(), item.getAmount());
          client.put(url, response -> {
            if (response.statusCode() == 200) {
              resultFuture.complete(); // need to check result?
            } else {
              resultFuture.fail(response.statusMessage());
            }
          })
            .exceptionHandler(resultFuture::fail)
            .end();
          return resultFuture;
        })
        .collect(Collectors.toList());
      // composite async results, all must be complete
      CompositeFuture.all(futures).setHandler(ar -> {
        if (ar.succeeded()) {
          future.complete();
        } else {
          future.fail(ar.cause());
        }
        ServiceDiscovery.releaseServiceObject(discovery, client); // Release the resources.
      });
      return future;
    });
  }
}

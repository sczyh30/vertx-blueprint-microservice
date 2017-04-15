package io.vertx.blueprint.microservice.cart.impl;

import io.vertx.blueprint.microservice.cache.CounterService;
import io.vertx.blueprint.microservice.cart.CartEvent;
import io.vertx.blueprint.microservice.cart.CheckoutResult;
import io.vertx.blueprint.microservice.cart.CheckoutService;
import io.vertx.blueprint.microservice.cart.ShoppingCart;
import io.vertx.blueprint.microservice.cart.ShoppingCartService;
import io.vertx.blueprint.microservice.common.functional.Functional;
import io.vertx.blueprint.microservice.order.Order;
import io.vertx.blueprint.microservice.product.ProductTuple;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.servicediscovery.types.HttpEndpoint;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple implementation for {@link CheckoutService}.
 *
 * @author Eric Zhao
 */
public class CheckoutServiceImpl implements CheckoutService {

  private final Vertx vertx;
  private final ServiceDiscovery discovery;

  public CheckoutServiceImpl(Vertx vertx, ServiceDiscovery discovery) {
    this.vertx = vertx;
    this.discovery = discovery;
  }

  @Override
  public void checkout(String userId, Handler<AsyncResult<CheckoutResult>> resultHandler) {
    if (userId == null) {
      resultHandler.handle(Future.failedFuture(new IllegalStateException("Invalid user")));
      return;
    }
    Future<ShoppingCart> cartFuture = getCurrentCart(userId);
    Future<CheckoutResult> orderFuture = cartFuture.compose(cart ->
      checkAvailableInventory(cart).compose(checkResult -> {
        if (checkResult.getBoolean("res")) {
          double totalPrice = calculateTotalPrice(cart);
          // create order instance
          Order order = new Order().setBuyerId(userId)
            .setPayId("TEST") // reserved field
            .setProducts(cart.getProductItems())
            .setTotalPrice(totalPrice);
          // set id and then send order, wait for reply
          return retrieveCounter("order")
            .compose(id -> sendOrderAwaitResult(order.setOrderId(id)))
            .compose(result -> saveCheckoutEvent(userId).map(v -> result));
        } else {
          // has insufficient inventory, fail
          return Future.succeededFuture(new CheckoutResult()
            .setMessage(checkResult.getString("message")));
        }
      })
    );

    orderFuture.setHandler(resultHandler);
  }

  /**
   * Fetch global counter of order from the cache infrastructure.
   *
   * @param key counter key (type)
   * @return async result of the counter
   */
  private Future<Long> retrieveCounter(String key) {
    Future<Long> future = Future.future();
    EventBusService.getProxy(discovery, CounterService.class,
      ar -> {
        if (ar.succeeded()) {
          CounterService service = ar.result();
          service.addThenRetrieve(key, future.completer());
        } else {
          future.fail(ar.cause());
        }
      });
    return future;
  }

  /**
   * Send the order to the order microservice and wait for reply.
   *
   * @param order order data object
   * @return async result
   */
  private Future<CheckoutResult> sendOrderAwaitResult(Order order) {
    Future<CheckoutResult> future = Future.future();
    vertx.eventBus().send(CheckoutService.ORDER_EVENT_ADDRESS, order.toJson(), reply -> {
      if (reply.succeeded()) {
        future.complete(new CheckoutResult((JsonObject) reply.result().body()));
      } else {
        future.fail(reply.cause());
      }
    });
    return future;
  }

  private Future<ShoppingCart> getCurrentCart(String userId) {
    Future<ShoppingCartService> future = Future.future();
    EventBusService.getProxy(discovery, ShoppingCartService.class, future.completer());
    return future.compose(service -> {
      Future<ShoppingCart> cartFuture = Future.future();
      service.getShoppingCart(userId, cartFuture.completer());
      return cartFuture.compose(c -> {
        if (c == null || c.isEmpty())
          return Future.failedFuture(new IllegalStateException("Invalid shopping cart"));
        else
          return Future.succeededFuture(c);
      });
    });
  }

  private double calculateTotalPrice(ShoppingCart cart) {
    return cart.getProductItems().stream()
      .map(p -> p.getAmount() * p.getPrice()) // join by product id
      .reduce(0.0d, (a, b) -> a + b);
  }

  private Future<HttpClient> getInventoryEndpoint() {
    Future<HttpClient> future = Future.future();
    HttpEndpoint.getClient(discovery,
      new JsonObject().put("name", "inventory-rest-api"),
      future.completer());
    return future;
  }

  private Future<JsonObject> getInventory(ProductTuple product, HttpClient client) {
    Future<Integer> future = Future.future();
    client.get("/" + product.getProductId(), response -> {
      if (response.statusCode() == 200) {
        response.bodyHandler(buffer -> {
          try {
            int inventory = Integer.valueOf(buffer.toString());
            future.complete(inventory);
          } catch (NumberFormatException ex) {
            future.fail(ex);
          }
        });
      } else {
        future.fail("not_found:" + product.getProductId());
      }
    })
      .exceptionHandler(future::fail)
      .end();
    return future.map(inv -> new JsonObject()
      .put("id", product.getProductId())
      .put("inventory", inv)
      .put("amount", product.getAmount()));
  }

  /**
   * Check inventory for the current cart.
   *
   * @param cart shopping cart data object
   * @return async result
   */
  private Future<JsonObject> checkAvailableInventory(ShoppingCart cart) {
    Future<List<JsonObject>> allInventories = getInventoryEndpoint().compose(client -> {
      List<Future<JsonObject>> futures = cart.getProductItems()
        .stream()
        .map(product -> getInventory(product, client))
        .collect(Collectors.toList());
      return Functional.allOfFutures(futures)
        .map(r -> {
          ServiceDiscovery.releaseServiceObject(discovery, client);
          return r;
        });
    });
    return allInventories.map(inventories -> {
      JsonObject result = new JsonObject();
      // get the list of products whose inventory is lower than the demand amount
      List<JsonObject> insufficient = inventories.stream()
        .filter(item -> item.getInteger("inventory") - item.getInteger("amount") < 0)
        .collect(Collectors.toList());
      // insufficient inventory exists
      if (insufficient.size() > 0) {
        String insufficientList = insufficient.stream()
          .map(item -> item.getString("id"))
          .collect(Collectors.joining(", "));
        result.put("message", String.format("Insufficient inventory available for product %s.", insufficientList))
          .put("res", false);
      } else {
        result.put("res", true);
      }
      return result;
    });
  }

  /**
   * Save checkout cart event for current user.
   *
   * @param userId user id
   * @return async result
   */
  private Future<Void> saveCheckoutEvent(String userId) {
    Future<ShoppingCartService> future = Future.future();
    EventBusService.getProxy(discovery, ShoppingCartService.class, future.completer());
    return future.compose(service -> {
      Future<Void> resFuture = Future.future();
      CartEvent event = CartEvent.createCheckoutEvent(userId);
      service.addCartEvent(event, resFuture.completer());
      return resFuture;
    });
  }

}

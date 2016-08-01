package io.vertx.blueprint.microservice.checkout.impl;

import io.vertx.blueprint.microservice.cache.CounterService;
import io.vertx.blueprint.microservice.checkout.CheckoutService;
import io.vertx.blueprint.microservice.common.entity.ProductTuple;
import io.vertx.blueprint.microservice.common.functional.Functional;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.servicediscovery.types.HttpEndpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A simple implementation for {@link CheckoutService}.
 */
public class CheckoutServiceImpl implements CheckoutService {

  private final Vertx vertx;
  private final ServiceDiscovery discovery;

  public CheckoutServiceImpl(Vertx vertx, ServiceDiscovery discovery) {
    this.vertx = vertx;
    this.discovery = discovery;
  }

  @Override
  public CheckoutService buy(String userId, List<ProductTuple> products, Handler<AsyncResult<JsonObject>> resultHandler) {
    if (userId == null) {
      resultHandler.handle(Future.failedFuture(new IllegalStateException("Invalid user")));
    } else if (products == null || products.isEmpty()) {
      resultHandler.handle(Future.failedFuture(new IllegalStateException("Empty product in the shopping request")));
    } else {
      retrieveProductRestClient()
        .compose(httpClient -> fetchProductsWithCurrentPrice(httpClient, products))
        .compose(resultPrice -> prepareAndRequestPayment(userId, products, resultPrice))
        .compose(this::sendRawOrder)
        .setHandler(resultHandler);
    }
    return this;
  }

  private Future<HttpClient> retrieveProductRestClient() {
    Future<HttpClient> clientFuture = Future.future();
    HttpEndpoint.getClient(discovery,
      new JsonObject().put("name", "product-rest-api"),
      clientFuture.completer());
    return clientFuture;
  }

  private Future<Double> fetchProductsWithCurrentPrice(HttpClient client, List<ProductTuple> products) {
    List<Future<JsonObject>> futures = new ArrayList<>();
    for (ProductTuple product : products) {
      Future<JsonObject> future = Future.future();
      client.get("/product/" + product.getProductId() + "/price", response -> {
        if (response.statusCode() == 200) {
          response.bodyHandler(buffer -> {
            JsonObject price = buffer.toJsonObject();
            future.complete(product.toJson().mergeIn(price));
          });
        } else {
          future.fail("not_found:" + product.getProductId());
        }
      })
        .exceptionHandler(future::fail)
        .end();
      futures.add(future);
    }
    return Functional.sequenceFuture(futures)
      .map(this::calculateTotalPrice); // calculate the total price of the products
  }

  private Future<JsonObject> prepareAndRequestPayment(String userId, List<ProductTuple> products, double totalPrice) {
    // get payment id counter
    return this.retrieveCounter("payment").compose(counter -> {
      Future<JsonObject> future = Future.future();

      // prepare necessary transaction request data
      JsonObject paymentRequest = new JsonObject().put("userId", userId)
        .put("payRawCounter", counter)
        .put("payAmount", totalPrice)
        .put("paySource", generatePaymentSource());
      // issue payment transaction request
      vertx.eventBus().send(PAYMENT_EVENT_ADDRESS, paymentRequest, ar -> { // TODO: SHOULD CHANGE
        if (ar.succeeded()) {
          // we need the payment data from the reply message
          JsonObject reply = ((JsonObject) ar.result().body())
            .put("buyerId", userId)
            .put("products", products)
            .put("totalPrice", totalPrice);
          future.complete(reply);
        } else {
          future.fail(ar.cause());
        }
      });
      return future;
    });
  }

  private Future<JsonObject> sendRawOrder(JsonObject rawOrder) {
    // get order id counter
    return this.retrieveCounter("order").compose(orderId -> {
      Future<JsonObject> future = Future.future();

      // set retrieved order id
      rawOrder.put("orderId", orderId);
      // submit order request
      vertx.eventBus().send(ORDER_EVENT_ADDRESS, rawOrder, ar -> { // TODO: SHOULD CHANGE
        if (ar.succeeded()) {
          // we need the order data from the reply message
          JsonObject reply = (JsonObject) ar.result().body();
          future.complete(reply);
        } else {
          future.fail(ar.cause());
        }
      });
      return future;
    });
  }

  private Future<Long> retrieveCounter(String key) {
    Future<Long> future = Future.future();
    EventBusService.<CounterService>getProxy(discovery,
      new JsonObject().put("name", "counter-eb-service"),
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

  private double calculateTotalPrice(List<JsonObject> products) {
    return products.stream()
      .map(e -> e.getDouble("price") * e.getInteger("amount"))
      .reduce(0.0, (x, y) -> x + y);
  }

  private short generatePaymentSource() {
    if (new Random().nextBoolean()) {
      return 1; // ZFB Payment
    } else {
      return 2; // Credit card payment
    }
  }

}

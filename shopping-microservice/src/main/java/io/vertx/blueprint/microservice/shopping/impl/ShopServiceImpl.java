package io.vertx.blueprint.microservice.shopping.impl;

import io.vertx.blueprint.microservice.common.entity.ProductTuple;
import io.vertx.blueprint.microservice.common.functional.Functional;
import io.vertx.blueprint.microservice.shopping.ShopService;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.impl.CompositeFutureImpl;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.HttpEndpoint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * A simple implementation for {@link io.vertx.blueprint.microservice.shopping.ShopService}.
 */
public class ShopServiceImpl implements ShopService {

  private static final String ORDER_ADDRESS = "service.order";

  private final Vertx vertx;
  private final ServiceDiscovery discovery;

  public ShopServiceImpl(Vertx vertx, ServiceDiscovery discovery) {
    this.vertx = vertx;
    this.discovery = discovery;
  }

  @Override
  public ShopService buy(String userId, List<ProductTuple> products, Handler<AsyncResult<JsonObject>> resultHandler) {
    if (userId == null) {
      resultHandler.handle(Future.failedFuture(new IllegalStateException("Invalid user")));
    } else if (products == null || products.isEmpty()) {
      resultHandler.handle(Future.failedFuture(new IllegalStateException("Empty product in the shopping request")));
    } else {
      retrieveProductRestClient()
        .compose(httpClient -> fetchProductsWithCurrentPrice(httpClient, products))
        .compose(completeProducts -> prepareAndSendOrder(userId, completeProducts))
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

  private Future<JsonArray> fetchProductsWithCurrentPrice(HttpClient client, List<ProductTuple> products) {
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
      .map(JsonArray::new);
  }

  private Future<JsonObject> prepareAndSendOrder(String userId, JsonArray products) {
    Future<JsonObject> future = Future.future();
    JsonObject orderRequest = new JsonObject().put("userId", userId)
      .put("products", products);
    vertx.eventBus().send(ORDER_ADDRESS, orderRequest, ar -> {
      if (ar.succeeded()) {
        JsonObject reply = (JsonObject) ar.result().body();
        future.complete(reply);
      } else {
        future.fail(ar.cause());
      }
    });
    return future;
  }

}

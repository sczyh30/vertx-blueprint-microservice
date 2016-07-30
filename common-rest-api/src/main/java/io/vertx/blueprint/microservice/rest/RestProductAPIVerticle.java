package io.vertx.blueprint.microservice.rest;

import io.vertx.blueprint.microservice.common.RestAPIVerticle;
import io.vertx.blueprint.microservice.product.Product;
import io.vertx.blueprint.microservice.product.ProductService;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.servicediscovery.types.EventBusService;


/**
 * This verticle exposes a HTTP endpoint to process shopping products management with REST APIs.
 *
 * @author Eric Zhao
 */
class RestProductAPIVerticle extends RestAPIVerticle {

  private static final String SERVICE_NAME = "product-rest-api";

  private static final String API_ADD = "/product";
  private static final String API_RETRIEVE = "/product/:productId";
  private static final String API_RETRIEVE_PRICE = "/product/:productId/price";
  private static final String API_RETRIEVE_ALL = "/products";
  private static final String API_UPDATE = "/product/:productId";
  private static final String API_DELETE = "/product/:productId";
  private static final String API_DELETE_ALL = "/product";

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();
    final Router router = Router.router(vertx);
    // body handler
    router.route().handler(BodyHandler.create());
    // API route handler
    router.post(API_ADD).handler(this::apiAdd);
    router.get(API_RETRIEVE).handler(this::apiRetrieve);
    router.get(API_RETRIEVE_PRICE).handler(this::apiRetrievePrice);
    router.get(API_RETRIEVE_ALL).handler(this::apiRetrieveAll);
    router.patch(API_UPDATE).handler(this::apiUpdate);
    router.delete(API_DELETE).handler(this::apiDelete);
    router.delete(API_DELETE_ALL).handler(this::apiDeleteAll);

    // get HTTP host and port from configuration, or use default value
    String host = config().getString("product.http.address", "0.0.0.0");
    int port = config().getInteger("product.http.port", 8082);

    // create HTTP server and publish REST service
    initProductDatabase()
      .compose(databaseOkay -> createHttpServer(router, host, port))
      .compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, host, port))
      .setHandler(future.completer());
  }

  private void callAsync(RoutingContext context, Handler<ProductService> handler) {
    EventBusService.<ProductService>getProxy(discovery,
      new JsonObject().put("name", ProductService.SERVICE_NAME),
      ar -> {
        if (ar.succeeded()) {
          handler.handle(ar.result());
        } else {
          serviceUnavailable(context, ar.cause());
        }
      });
  }

  private void apiAdd(RoutingContext context) {
    try {
      Product product = new Product(new JsonObject(context.getBodyAsString()));
      callAsync(context, service -> {
        service.addProduct(product, resultHandler(context, r -> {
          String result = new JsonObject().put("message", "product_added")
            .put("productId", product.getProductId())
            .encodePrettily();
          context.response().setStatusCode(201)
            .putHeader("content-type", "application/json")
            .end(result);
        }));
      });
    } catch (DecodeException e) {
      badRequest(context, e);
    }
  }

  private void apiRetrieve(RoutingContext context) {
    String productId = context.request().getParam("productId");
    callAsync(context, service -> {
      service.retrieveProduct(productId, resultHandler(context, product -> {
        if (product == null) {
          notFound(context);
        } else {
          context.response()
            .putHeader("content-type", "application/json")
            .end(product.toString());
        }
      }));
    });
  }

  private void apiRetrievePrice(RoutingContext context) {
    String productId = context.request().getParam("productId");
    callAsync(context, service -> {
      service.retrieveProductPrice(productId, resultHandler(context, price -> {
        if (price == null) {
          notFound(context);
        } else {
          context.response()
            .putHeader("content-type", "application/json")
            .end(price.encode());
        }
      }));
    });
  }

  private void apiRetrieveAll(RoutingContext context) {
    callAsync(context, service -> {
      service.retrieveAllProducts(resultHandler(context, result -> {
        if (result == null) {
          serviceUnavailable(context);
        } else {
          final String encoded = Json.encodePrettily(result);
          context.response()
            .putHeader("content-type", "application/json")
            .end(encoded);
        }
      }));
    });
  }

  private void apiUpdate(RoutingContext context) {
    notImplemented(context);
  }

  private void apiDelete(RoutingContext context) {
    String productId = context.request().getParam("productId");
    callAsync(context, service -> {
      service.deleteProduct(productId, deleteResultHandler(context));
    });
  }

  private void apiDeleteAll(RoutingContext context) {
    callAsync(context, service -> {
      service.deleteAllProducts(deleteResultHandler(context));
    });
  }

  // Helper methods

  private Future<Void> initProductDatabase() {
    Future<ProductService> serviceFuture = Future.future();
    EventBusService.getProxy(discovery,
      new JsonObject().put("name", ProductService.SERVICE_NAME),
      serviceFuture.completer()
    );
    return serviceFuture.compose(service -> {
      Future<Void> initFuture = Future.future();
      service.initializePersistence(initFuture.completer());
      return initFuture;
    });
  }

}

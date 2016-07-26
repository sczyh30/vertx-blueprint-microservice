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
import io.vertx.servicediscovery.ServiceReference;

/**
 * This verticle exposes a HTTP endpoint to process shopping products management with REST APIs.
 *
 * @author Eric Zhao
 */
public class RestProductAPIVerticle extends RestAPIVerticle {

  public static final String API_ADD = "/product";
  public static final String API_RETRIEVE = "/product/:productId";
  public static final String API_RETRIEVE_ALL = "/product";
  public static final String API_UPDATE = "/product/:productId";
  public static final String API_DELETE = "/product/:productId";
  public static final String API_DELETE_ALL = "/product";

  @Override
  public void start(Future<Void> future) throws Exception {
    final Router router = Router.router(vertx);
    // body handler
    router.route().handler(BodyHandler.create());
    // api route handler
    router.put(API_ADD).handler(this::apiAdd);
    router.get(API_RETRIEVE).handler(this::apiRetrieve);
    router.get(API_RETRIEVE_ALL).handler(this::apiRetrieveAll);
    router.patch(API_UPDATE).handler(this::apiUpdate);
    router.delete(API_DELETE).handler(this::apiDelete);
    router.delete(API_DELETE_ALL).handler(this::apiDeleteAll);

    // create http server for the REST service
    vertx.createHttpServer()
      .requestHandler(router::accept)
      .listen(config().getInteger("user.http.port", 8082),
        config().getString("user.http.address", "0.0.0.0"), result -> {
          if (result.succeeded()) {
            future.complete();
          } else {
            future.fail(result.cause());
          }
        });
  }

  private void callAsync(RoutingContext context, Handler<ProductService> handler) {
    discovery.getRecord(new JsonObject().put("name", "product-eb-service"), ar -> {
      if (ar.succeeded()) {
        if (ar.result() != null) {
          ServiceReference reference = discovery.getReference(ar.result());
          ProductService service = reference.get();
          handler.handle(service);
          reference.release();
        }
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

}

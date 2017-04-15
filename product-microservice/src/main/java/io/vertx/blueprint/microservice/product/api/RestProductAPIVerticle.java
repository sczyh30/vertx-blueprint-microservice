package io.vertx.blueprint.microservice.product.api;

import io.vertx.blueprint.microservice.common.RestAPIVerticle;
import io.vertx.blueprint.microservice.product.Product;
import io.vertx.blueprint.microservice.product.ProductService;
import io.vertx.core.Future;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;


/**
 * This verticle exposes a HTTP endpoint to process shopping products management with REST APIs.
 *
 * @author Eric Zhao
 */
public class RestProductAPIVerticle extends RestAPIVerticle {

  public static final String SERVICE_NAME = "product-rest-api";

  private static final String API_ADD = "/add";
  private static final String API_RETRIEVE_BY_PAGE = "/products";
  private static final String API_RETRIEVE_ALL = "/products";
  private static final String API_RETRIEVE_PRICE = "/:productId/price";
  private static final String API_RETRIEVE = "/:productId";
  private static final String API_UPDATE = "/:productId";
  private static final String API_DELETE = "/:productId";
  private static final String API_DELETE_ALL = "/all";

  private final ProductService service;

  public RestProductAPIVerticle(ProductService service) {
    this.service = service;
  }

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();
    final Router router = Router.router(vertx);
    // body handler
    router.route().handler(BodyHandler.create());
    // API route handler
    router.post(API_ADD).handler(this::apiAdd);
    router.get(API_RETRIEVE_BY_PAGE).handler(this::apiRetrieveByPage);
    router.get(API_RETRIEVE_ALL).handler(this::apiRetrieveAll);
    router.get(API_RETRIEVE_PRICE).handler(this::apiRetrievePrice);
    router.get(API_RETRIEVE).handler(this::apiRetrieve);
    router.patch(API_UPDATE).handler(this::apiUpdate);
    router.delete(API_DELETE).handler(this::apiDelete);
    router.delete(API_DELETE_ALL).handler(context -> requireLogin(context, this::apiDeleteAll));

    // get HTTP host and port from configuration, or use default value
    String host = config().getString("product.http.address", "0.0.0.0");
    int port = config().getInteger("product.http.port", 8082);

    // create HTTP server and publish REST service
    createHttpServer(router, host, port)
      .compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, host, port))
      .setHandler(future.completer());
  }

  private void apiAdd(RoutingContext context) {
    try {
      Product product = new Product(new JsonObject(context.getBodyAsString()));
      service.addProduct(product, resultHandler(context, r -> {
        String result = new JsonObject().put("message", "product_added")
          .put("productId", product.getProductId())
          .encodePrettily();
        context.response().setStatusCode(201)
          .putHeader("content-type", "application/json")
          .end(result);
      }));
    } catch (DecodeException e) {
      badRequest(context, e);
    }
  }

  private void apiRetrieve(RoutingContext context) {
    String productId = context.request().getParam("productId");
    service.retrieveProduct(productId, resultHandlerNonEmpty(context));
  }

  private void apiRetrievePrice(RoutingContext context) {
    String productId = context.request().getParam("productId");
    service.retrieveProductPrice(productId, resultHandlerNonEmpty(context));
  }

  private void apiRetrieveByPage(RoutingContext context) {
    try {
      String p = context.request().getParam("p");
      int page = p == null ? 1 : Integer.parseInt(p);
      service.retrieveProductsByPage(page, resultHandler(context, Json::encodePrettily));
    } catch (Exception ex) {
      badRequest(context, ex);
    }
  }

  private void apiRetrieveAll(RoutingContext context) {
    service.retrieveAllProducts(resultHandler(context, Json::encodePrettily));
  }

  private void apiUpdate(RoutingContext context) {
    notImplemented(context);
  }

  private void apiDelete(RoutingContext context) {
    String productId = context.request().getParam("productId");
    service.deleteProduct(productId, deleteResultHandler(context));
  }

  private void apiDeleteAll(RoutingContext context, JsonObject principle) {
    service.deleteAllProducts(deleteResultHandler(context));
  }

}

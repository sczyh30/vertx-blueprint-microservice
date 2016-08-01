package io.vertx.blueprint.microservice.account.impl;

import io.vertx.blueprint.microservice.common.RestAPIVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * This verticle exposes a HTTP endpoint to process user data via REST API.
 *
 * @author Eric Zhao
 */
class RestUserAccountAPIVerticle extends RestAPIVerticle {

  private static final String SERVICE_NAME = "user-account-rest-api";

  private static final String API_ADD = "/user";
  private static final String API_RETRIEVE = "/user/:id";
  private static final String API_RETRIEVE_ALL = "/user";
  private static final String API_UPDATE = "/user/:id";
  private static final String API_DELETE = "/user/:id";
  private static final String API_DELETE_ALL = "/user";

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();
    final Router router = Router.router(vertx);
    // body handler
    router.route().handler(BodyHandler.create());
    // api route handler
    router.post(API_ADD).handler(this::apiAddUser);
    router.get(API_RETRIEVE).handler(this::apiRetrieveUser);
    router.get(API_RETRIEVE_ALL).handler(this::apiRetrieveAll);
    router.patch(API_UPDATE).handler(this::apiUpdateUser);
    router.delete(API_DELETE).handler(this::apiDeleteUser);
    router.delete(API_DELETE_ALL).handler(this::apiDeleteAll);

    String host = config().getString("user.account.http.address", "0.0.0.0");
    int port = config().getInteger("user.account.http.port", 8081);

    // create HTTP server and publish REST service
    createHttpServer(router, host, port)
      .compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, host, port))
      .setHandler(future.completer());
  }

  private void apiAddUser(RoutingContext context) {

  }

  private void apiRetrieveUser(RoutingContext context) {

  }

  private void apiRetrieveAll(RoutingContext context) {

  }

  private void apiUpdateUser(RoutingContext context) {

  }

  private void apiDeleteUser(RoutingContext context) {

  }

  private void apiDeleteAll(RoutingContext context) {

  }

}

package io.vertx.blueprint.microservice.user;

import io.vertx.blueprint.microservice.common.RestAPIVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * This verticle exposes a HTTP endpoint to retrieve user data via REST API.
 *
 * @author Eric Zhao
 */
public class RestUserAPIVerticle extends RestAPIVerticle {

  public static final String API_ADD = "/user";
  public static final String API_RETRIEVE = "/user/:id";
  public static final String API_RETRIEVE_ALL = "/user";
  public static final String API_UPDATE = "/user/:id";
  public static final String API_DELETE = "/user/:id";
  public static final String API_DELETE_ALL = "/user";

  @Override
  public void start(Future<Void> future) throws Exception {
    final Router router = Router.router(vertx);
    // body handler
    router.route().handler(BodyHandler.create());
    // api route handler
    router.put(API_ADD).handler(this::apiAddUser);
    router.get(API_RETRIEVE).handler(this::apiRetrieveUser);
    router.get(API_RETRIEVE_ALL).handler(this::apiRetrieveAll);
    router.patch(API_UPDATE).handler(this::apiUpdateUser);
    router.delete(API_DELETE).handler(this::apiDeleteUser);
    router.delete(API_DELETE_ALL).handler(this::apiDeleteAll);

    // create http server for the REST service
    vertx.createHttpServer()
      .requestHandler(router::accept)
      .listen(config().getInteger("user.http.port", 8081),
        config().getString("user.http.address", "0.0.0.0"), result -> {
          if (result.succeeded()) {
            future.complete();
          } else {
            future.fail(result.cause());
          }
        });
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

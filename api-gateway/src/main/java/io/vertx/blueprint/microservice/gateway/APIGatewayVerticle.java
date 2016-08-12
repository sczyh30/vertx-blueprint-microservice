package io.vertx.blueprint.microservice.gateway;

import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.OAuth2AuthHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.UserSessionHandler;
import io.vertx.ext.web.sstore.ClusteredSessionStore;
import io.vertx.servicediscovery.rest.ServiceDiscoveryRestEndpoint;

/**
 * A verticle for global API gateway.
 * This API gateway uses HTTP-HTTP pattern.
 */
public class APIGatewayVerticle extends BaseMicroserviceVerticle {

  private static final Logger logger = LoggerFactory.getLogger(APIGatewayVerticle.class);

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();

    Router router = Router.router(vertx);
    // cookie and session handler
    router.route().handler(CookieHandler.create());
    router.route().handler(SessionHandler.create(
      ClusteredSessionStore.create(vertx, "shopping.user.auth.session")));

    OAuth2Auth oauth2 = OAuth2Auth
      .createKeycloak(vertx, OAuth2FlowType.AUTH_CODE, config());

    router.route().handler(UserSessionHandler.create(oauth2));

    OAuth2AuthHandler authHandler = OAuth2AuthHandler.create(oauth2, "/"); // TODO

    authHandler.setupCallback(router.route("/api/login")); // TODO
    router.route("/api/*").handler(authHandler);

    // TODO: API gateway : http-http / http-eb
    // Issue: if we use http-http, we should first retrieve all rest endpoints from discovery infrastructure
    // then forward the requests to the corresponding endpoint using a specific pattern.
    // HTTP-EventBus seems to be convenient but it does not support pattern match (like route).

    // discovery endpoint
    ServiceDiscoveryRestEndpoint.create(router, discovery);

    // get HTTP host and port from configuration, or use default value
    String host = config().getString("api.gateway.http.address", "0.0.0.0");
    int port = config().getInteger("api.gateway.http.port", 8787);

    vertx.createHttpServer()
      .requestHandler(router::accept)
      .listen(port, host, ar -> {
        if (ar.succeeded()) {
          future.complete();
          logger.info("API Gateway is running on port " + port);
        } else {
          future.fail(ar.cause());
        }
      });

  }

}

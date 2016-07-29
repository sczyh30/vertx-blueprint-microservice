package io.vertx.blueprint.microservice.shopping.ui;

import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.servicediscovery.rest.ServiceDiscoveryRestEndpoint;

/**
 * A verticle providing a simple shopping web UI.
 *
 * @author Eric Zhao
 */
public class ShoppingUIVerticle extends BaseMicroserviceVerticle {

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start();
    Router router = Router.router(vertx);

    // event bus bridge
    SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
    router.route("/eventbus/*").handler(sockJSHandler);

    // discovery endpoint
    ServiceDiscoveryRestEndpoint.create(router, discovery);

    // Static content
    router.route("/*").handler(StaticHandler.create());

    // create HTTP server
    vertx.createHttpServer()
      .requestHandler(router::accept)
      .listen(config().getInteger("shopping.ui.http.port", 8080), ar -> {
        if (ar.succeeded()) {
          future.complete();
        } else {
          future.fail(ar.cause());
        }
      });
  }
}

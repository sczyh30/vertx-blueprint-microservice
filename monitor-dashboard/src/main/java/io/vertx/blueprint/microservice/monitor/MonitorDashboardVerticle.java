package io.vertx.blueprint.microservice.monitor;

import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.servicediscovery.rest.ServiceDiscoveryRestEndpoint;

/**
 * The monitor dashboard of the microservice application.
 *
 * @author Eric Zhao
 */
public class MonitorDashboardVerticle extends BaseMicroserviceVerticle {

  @Override
  public void start() throws Exception {
    super.start();
    Router router = Router.router(vertx);

    // Event bus bridge
    SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
    BridgeOptions options = new BridgeOptions();

    sockJSHandler.bridge(options);
    router.route("/eventbus/*").handler(sockJSHandler);

    // Discovery endpoint
    ServiceDiscoveryRestEndpoint.create(router, discovery);

    // Static content
    router.route("/*").handler(StaticHandler.create());

    vertx.createHttpServer()
      .requestHandler(router::accept)
      .listen(9100);
  }
}

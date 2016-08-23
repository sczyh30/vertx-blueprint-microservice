package io.vertx.blueprint.microservice.monitor;

import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.dropwizard.MetricsService;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
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

    // create Dropwizard metrics service
    MetricsService service = MetricsService.create(vertx);

    // event bus bridge
    SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
    BridgeOptions options = new BridgeOptions()
      .addOutboundPermitted(new PermittedOptions().setAddress("microservice.monitor.metrics"))
      .addOutboundPermitted(new PermittedOptions().setAddress("events.log"));

    sockJSHandler.bridge(options);
    router.route("/eventbus/*").handler(sockJSHandler);

    // discovery endpoint
    ServiceDiscoveryRestEndpoint.create(router, discovery);

    // static content
    router.route("/*").handler(StaticHandler.create());

    int port = config().getInteger("monitor.http.port", 9100);
    String host = config().getString("monitor.http.host", "0.0.0.0");
    int metricsInterval = config().getInteger("monitor.metrics.interval", 5000);

    vertx.createHttpServer()
      .requestHandler(router::accept)
      .listen(port, host);

    // send metrics message to the event bus
    vertx.setPeriodic(metricsInterval, t -> {
      JsonObject metrics = service.getMetricsSnapshot(vertx);
      vertx.eventBus().publish("microservice.monitor.metrics", metrics);
    });
  }
}

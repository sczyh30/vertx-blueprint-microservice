package io.vertx.blueprint.microservice.common.config;

import java.net.URI;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.apache.logging.log4j.core.LoggerContext;
import rx.functions.Action1;

import static java.util.Objects.isNull;
import static org.apache.logging.log4j.core.config.Configurator.initialize;

public class Log4jConfigurationServiceHandler {

  private static final Logger logger = LoggerFactory.getLogger(Log4jConfigurationServiceHandler.class);
  private static LoggerContext loggerCtx;

  public static Action1<JsonObject> log4jSubscriber = config -> {
    if (isNull(loggerCtx)) {
      initLoggerContext(config);
    } else {
      updateLoggerContext(config);
    }
  };

  private static LoggerContext initLoggerContext(final JsonObject config) {
    return loggerCtx = initialize(null, getLog4jConfigUri(config));
  }

  private static void updateLoggerContext(final JsonObject config) {
    try {
      loggerCtx.setConfigLocation(new URI(getLog4jConfigUri(config)));
    } catch (final Exception e) {
      logger.error(e);
    }
  }

  private static String getLog4jConfigUri(final JsonObject config) {
    return config.getString("log4j.config.uri", "log4j2.xml");
  }
}
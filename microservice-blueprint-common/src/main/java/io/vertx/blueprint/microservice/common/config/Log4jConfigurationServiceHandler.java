package io.vertx.blueprint.microservice.common.config;

import java.net.URI;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;

public class Log4jConfigurationServiceHandler {

  private static final Logger logger = LoggerFactory.getLogger(Log4jConfigurationServiceHandler.class);
  private static LoggerContext loggerCtx;

  public static ConfigurationServiceInitHandler log4jInitHandler = config -> {
    loggerCtx = Configurator.initialize(null, config.getString("log4j.config.uri", "log4j2.xml"));
  };

  public static ConfigurationServiceUpdateHandler log4jUpdateHandler = config -> {
    try {
      loggerCtx.setConfigLocation(new URI(config.getString("log4j.config.uri", "log4j2.xml")));
    } catch (final Exception e) {
      logger.error(e);
    }
  };
}

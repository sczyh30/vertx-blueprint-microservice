package io.vertx.blueprint.microservice.common.discovery.impl;

import io.vertx.blueprint.microservice.common.discovery.OAuth2Service;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2ClientOptions;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;
import io.vertx.servicediscovery.types.AbstractServiceReference;

import java.util.Objects;

/**
 * Implementation of {@link OAuth2Service}.
 *
 * @author Eric Zhao
 */
public class OAuth2ServiceImpl implements OAuth2Service {

  @Override
  public String name() {
    return TYPE;
  }

  @Override
  public ServiceReference get(Vertx vertx, ServiceDiscovery discovery, Record record, JsonObject configuration) {
    Objects.requireNonNull(vertx);
    Objects.requireNonNull(record);
    Objects.requireNonNull(discovery);
    return new OAuth2ServiceReference(vertx, discovery, record, configuration);
  }

  /**
   * A reference on a {@link OAuth2Auth} provider.
   */
  private class OAuth2ServiceReference extends AbstractServiceReference<OAuth2Auth> {

    private JsonObject config;

    OAuth2ServiceReference(Vertx vertx, ServiceDiscovery discovery, Record record, JsonObject configuration) {
      super(vertx, discovery, record);
      this.config = configuration;
    }

    @Override
    protected OAuth2Auth retrieve() {
      JsonObject authConfig = record().getMetadata().copy()
        .mergeIn(record().getLocation());

      if (config != null) {
        authConfig.mergeIn(config);
      }

      OAuth2FlowType flow = OAuth2FlowType.valueOf(authConfig.getString("flow.type", "AUTH_CODE")
        .toUpperCase());

      if (authConfig.getBoolean("type.keycloak")) {
        return OAuth2Auth.createKeycloak(vertx, flow, authConfig);
      } else {
        return OAuth2Auth.create(vertx, flow, new OAuth2ClientOptions(authConfig));
      }
    }
  }
}

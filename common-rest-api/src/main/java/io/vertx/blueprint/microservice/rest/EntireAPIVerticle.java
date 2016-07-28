package io.vertx.blueprint.microservice.rest;

import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;

/**
 * A verticle provides all of the REST APIs.
 */
public class EntireAPIVerticle extends BaseMicroserviceVerticle {

  @Override
  public void start() throws Exception {
    super.start();
    vertx.deployVerticle(new RestProductAPIVerticle());
  }

}

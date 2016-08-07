/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.vertx.blueprint.microservice.cart;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter for {@link io.vertx.blueprint.microservice.cart.CheckoutResult}.
 *
 * NOTE: This class has been automatically generated from the {@link io.vertx.blueprint.microservice.cart.CheckoutResult} original class using Vert.x codegen.
 */
public class CheckoutResultConverter {

  public static void fromJson(JsonObject json, CheckoutResult obj) {
    if (json.getValue("message") instanceof String) {
      obj.setMessage((String)json.getValue("message"));
    }
    if (json.getValue("order") instanceof JsonObject) {
      obj.setOrder(new io.vertx.blueprint.microservice.order.Order((JsonObject)json.getValue("order")));
    }
  }

  public static void toJson(CheckoutResult obj, JsonObject json) {
    if (obj.getMessage() != null) {
      json.put("message", obj.getMessage());
    }
    if (obj.getOrder() != null) {
      json.put("order", obj.getOrder().toJson());
    }
  }
}
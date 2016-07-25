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

package io.vertx.blueprint.microservice.product;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter for {@link io.vertx.blueprint.microservice.product.Product}.
 *
 * NOTE: This class has been automatically generated from the {@link io.vertx.blueprint.microservice.product.Product} original class using Vert.x codegen.
 */
public class ProductConverter {

  public static void fromJson(JsonObject json, Product obj) {
    if (json.getValue("illustration") instanceof String) {
      obj.setIllustration((String)json.getValue("illustration"));
    }
    if (json.getValue("name") instanceof String) {
      obj.setName((String)json.getValue("name"));
    }
    if (json.getValue("price") instanceof Number) {
      obj.setPrice(((Number)json.getValue("price")).doubleValue());
    }
    if (json.getValue("productId") instanceof String) {
      obj.setProductId((String)json.getValue("productId"));
    }
  }

  public static void toJson(Product obj, JsonObject json) {
    if (obj.getIllustration() != null) {
      json.put("illustration", obj.getIllustration());
    }
    if (obj.getName() != null) {
      json.put("name", obj.getName());
    }
    json.put("price", obj.getPrice());
    if (obj.getProductId() != null) {
      json.put("productId", obj.getProductId());
    }
  }
}
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
 * Converter for {@link io.vertx.blueprint.microservice.cart.ShoppingCart}.
 *
 * NOTE: This class has been automatically generated from the {@link io.vertx.blueprint.microservice.cart.ShoppingCart} original class using Vert.x codegen.
 */
public class ShoppingCartConverter {

  public static void fromJson(JsonObject json, ShoppingCart obj) {
    if (json.getValue("priceMap") instanceof JsonObject) {
      java.util.Map<String, java.lang.Double> map = new java.util.LinkedHashMap<>();
      json.getJsonObject("priceMap").forEach(entry -> {
        if (entry.getValue() instanceof Number)
          map.put(entry.getKey(), ((Number)entry.getValue()).doubleValue());
      });
      obj.setPriceMap(map);
    }
    if (json.getValue("productItems") instanceof JsonArray) {
      java.util.ArrayList<io.vertx.blueprint.microservice.product.ProductTuple> list = new java.util.ArrayList<>();
      json.getJsonArray("productItems").forEach( item -> {
        if (item instanceof JsonObject)
          list.add(new io.vertx.blueprint.microservice.product.ProductTuple((JsonObject)item));
      });
      obj.setProductItems(list);
    }
  }

  public static void toJson(ShoppingCart obj, JsonObject json) {
    json.put("empty", obj.isEmpty());
    if (obj.getPriceMap() != null) {
      JsonObject map = new JsonObject();
      obj.getPriceMap().forEach((key,value) -> map.put(key, value));
      json.put("priceMap", map);
    }
    if (obj.getProductItems() != null) {
      json.put("productItems", new JsonArray(
          obj.getProductItems().
              stream().
              map(item -> item.toJson()).
              collect(java.util.stream.Collectors.toList())));
    }
  }
}
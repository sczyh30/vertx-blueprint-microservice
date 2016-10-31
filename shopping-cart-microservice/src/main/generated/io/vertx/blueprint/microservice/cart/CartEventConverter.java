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
 * Converter for {@link io.vertx.blueprint.microservice.cart.CartEvent}.
 *
 * NOTE: This class has been automatically generated from the {@link io.vertx.blueprint.microservice.cart.CartEvent} original class using Vert.x codegen.
 */
public class CartEventConverter {

  public static void fromJson(JsonObject json, CartEvent obj) {
    if (json.getValue("amount") instanceof Number) {
      obj.setAmount(((Number)json.getValue("amount")).intValue());
    }
    if (json.getValue("cartEventType") instanceof String) {
      obj.setCartEventType(io.vertx.blueprint.microservice.cart.CartEventType.valueOf((String)json.getValue("cartEventType")));
    }
    if (json.getValue("createdAt") instanceof Number) {
      obj.setCreatedAt(((Number)json.getValue("createdAt")).longValue());
    }
    if (json.getValue("id") instanceof Number) {
      obj.setId(((Number)json.getValue("id")).longValue());
    }
    if (json.getValue("productId") instanceof String) {
      obj.setProductId((String)json.getValue("productId"));
    }
    if (json.getValue("userId") instanceof String) {
      obj.setUserId((String)json.getValue("userId"));
    }
  }

  public static void toJson(CartEvent obj, JsonObject json) {
    if (obj.getAmount() != null) {
      json.put("amount", obj.getAmount());
    }
    if (obj.getCartEventType() != null) {
      json.put("cartEventType", obj.getCartEventType().name());
    }
    if (obj.getCreatedAt() != null) {
      json.put("createdAt", obj.getCreatedAt());
    }
    if (obj.getId() != null) {
      json.put("id", obj.getId());
    }
    if (obj.getProductId() != null) {
      json.put("productId", obj.getProductId());
    }
    if (obj.getUserId() != null) {
      json.put("userId", obj.getUserId());
    }
  }
}
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

package io.vertx.blueprint.microservice.store;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter for {@link io.vertx.blueprint.microservice.store.Store}.
 *
 * NOTE: This class has been automatically generated from the {@link io.vertx.blueprint.microservice.store.Store} original class using Vert.x codegen.
 */
public class StoreConverter {

  public static void fromJson(JsonObject json, Store obj) {
    if (json.getValue("description") instanceof String) {
      obj.setDescription((String)json.getValue("description"));
    }
    if (json.getValue("name") instanceof String) {
      obj.setName((String)json.getValue("name"));
    }
    if (json.getValue("openTime") instanceof Number) {
      obj.setOpenTime(((Number)json.getValue("openTime")).longValue());
    }
    if (json.getValue("sellerId") instanceof String) {
      obj.setSellerId((String)json.getValue("sellerId"));
    }
  }

  public static void toJson(Store obj, JsonObject json) {
    if (obj.getDescription() != null) {
      json.put("description", obj.getDescription());
    }
    if (obj.getName() != null) {
      json.put("name", obj.getName());
    }
    if (obj.getOpenTime() != null) {
      json.put("openTime", obj.getOpenTime());
    }
    if (obj.getSellerId() != null) {
      json.put("sellerId", obj.getSellerId());
    }
  }
}
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

package io.vertx.blueprint.microservice.order;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter for {@link io.vertx.blueprint.microservice.order.Order}.
 *
 * NOTE: This class has been automatically generated from the {@link io.vertx.blueprint.microservice.order.Order} original class using Vert.x codegen.
 */
public class OrderConverter {

  public static void fromJson(JsonObject json, Order obj) {
    if (json.getValue("buyerId") instanceof String) {
      obj.setBuyerId((String)json.getValue("buyerId"));
    }
    if (json.getValue("createTime") instanceof Number) {
      obj.setCreateTime(((Number)json.getValue("createTime")).longValue());
    }
    if (json.getValue("orderId") instanceof Number) {
      obj.setOrderId(((Number)json.getValue("orderId")).longValue());
    }
    if (json.getValue("productId") instanceof String) {
      obj.setProductId((String)json.getValue("productId"));
    }
    if (json.getValue("sellerId") instanceof String) {
      obj.setSellerId((String)json.getValue("sellerId"));
    }
    if (json.getValue("totalPrice") instanceof Number) {
      obj.setTotalPrice(((Number)json.getValue("totalPrice")).doubleValue());
    }
  }

  public static void toJson(Order obj, JsonObject json) {
    if (obj.getBuyerId() != null) {
      json.put("buyerId", obj.getBuyerId());
    }
    if (obj.getCreateTime() != null) {
      json.put("createTime", obj.getCreateTime());
    }
    if (obj.getOrderId() != null) {
      json.put("orderId", obj.getOrderId());
    }
    if (obj.getProductId() != null) {
      json.put("productId", obj.getProductId());
    }
    if (obj.getSellerId() != null) {
      json.put("sellerId", obj.getSellerId());
    }
    if (obj.getTotalPrice() != null) {
      json.put("totalPrice", obj.getTotalPrice());
    }
  }
}
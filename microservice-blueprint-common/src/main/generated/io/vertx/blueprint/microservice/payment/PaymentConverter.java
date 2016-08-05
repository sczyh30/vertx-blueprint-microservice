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

package io.vertx.blueprint.microservice.payment;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter for {@link io.vertx.blueprint.microservice.payment.Payment}.
 *
 * NOTE: This class has been automatically generated from the {@link io.vertx.blueprint.microservice.payment.Payment} original class using Vert.x codegen.
 */
public class PaymentConverter {

  public static void fromJson(JsonObject json, Payment obj) {
    if (json.getValue("payAmount") instanceof Number) {
      obj.setPayAmount(((Number)json.getValue("payAmount")).doubleValue());
    }
    if (json.getValue("payId") instanceof String) {
      obj.setPayId((String)json.getValue("payId"));
    }
    if (json.getValue("paySource") instanceof Number) {
      obj.setPaySource(((Number)json.getValue("paySource")).shortValue());
    }
    if (json.getValue("paymentTime") instanceof Number) {
      obj.setPaymentTime(((Number)json.getValue("paymentTime")).longValue());
    }
  }

  public static void toJson(Payment obj, JsonObject json) {
    if (obj.getPayAmount() != null) {
      json.put("payAmount", obj.getPayAmount());
    }
    if (obj.getPayId() != null) {
      json.put("payId", obj.getPayId());
    }
    if (obj.getPaySource() != null) {
      json.put("paySource", obj.getPaySource());
    }
    if (obj.getPaymentTime() != null) {
      json.put("paymentTime", obj.getPaymentTime());
    }
  }
}
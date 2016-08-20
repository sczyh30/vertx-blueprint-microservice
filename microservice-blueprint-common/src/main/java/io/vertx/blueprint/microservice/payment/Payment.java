package io.vertx.blueprint.microservice.payment;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Payment data object.
 *
 * @author Eric Zhao
 */
@DataObject(generateConverter = true)
public class Payment {

  private String payId;
  private Double payAmount;
  private Short paySource;
  private Long paymentTime;

  public Payment() {
    // Empty constructor
  }

  public Payment(JsonObject json) {
    PaymentConverter.fromJson(json, this);
  }

  public Payment(Payment other) {
    this.payId = other.payId;
    this.payAmount = other.payAmount;
    this.paySource = other.paySource;
    this.paymentTime = other.paymentTime;
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    PaymentConverter.toJson(this, json);
    return json;
  }

  public Payment(Long payCounter, Double payAmount, Short paySource) {
    initId(payCounter);
    this.payAmount = payAmount;
    this.paySource = paySource;
    this.paymentTime = System.currentTimeMillis();
  }

  public String getPayId() {
    return payId;
  }

  public Payment setPayId(String payId) {
    this.payId = payId;
    return this;
  }

  public Double getPayAmount() {
    return payAmount;
  }

  public Payment setPayAmount(Double payAmount) {
    this.payAmount = payAmount;
    return this;
  }

  public Short getPaySource() {
    return paySource;
  }

  public Payment setPaySource(Short paySource) {
    this.paySource = paySource;
    return this;
  }

  public Long getPaymentTime() {
    return paymentTime;
  }

  public Payment setPaymentTime(Long paymentTime) {
    this.paymentTime = paymentTime;
    return this;
  }

  void initId(Long counter) {
    if (counter < 0) {
      throw new IllegalStateException("Negative counter");
    }
    if (this.payId != null && !this.payId.equals("")) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
      String datePrefix = LocalDate.now().format(formatter);
      int mod = 12 - (int) Math.sqrt(counter);
      char[] zeroChars = new char[mod];
      for (int i = 0; i < mod; i++) {
        zeroChars[i] = '0';
      }
      String zs = new String(zeroChars);
      this.payId = datePrefix + zs + counter;
    }
  }

  @Override
  public String toString() {
    return this.toJson().encodePrettily();
  }
}

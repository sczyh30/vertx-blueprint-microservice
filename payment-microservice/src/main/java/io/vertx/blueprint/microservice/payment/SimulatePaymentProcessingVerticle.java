package io.vertx.blueprint.microservice.payment;

import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.blueprint.microservice.payment.impl.PaymentQueryServiceImpl;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.types.MessageSource;
import io.vertx.serviceproxy.ProxyHelper;

/**
 * A verticle that simulates payment processing.
 */
public class SimulatePaymentProcessingVerticle extends BaseMicroserviceVerticle {

  private PaymentQueryService pqs;

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start(future);

    pqs = new PaymentQueryServiceImpl(vertx, config());
    ProxyHelper.registerService(PaymentQueryService.class, vertx, pqs, PaymentQueryService.SERVICE_ADDRESS);

    initPaymentPersistence(pqs)
      .compose(persistenceOkay -> publishEventBusService(PaymentQueryService.SERVICE_NAME,
        PaymentQueryService.SERVICE_ADDRESS, PaymentQueryService.class))
      .compose(servicePublished -> receiveAndProcess())
      .setHandler(future.completer());
  }

  private Future<Void> receiveAndProcess() {
    Future<Void> future = Future.future();
    MessageSource.<JsonObject>getConsumer(discovery,
      new JsonObject().put("name", "shopping-payment-message-source"),
      ar -> {
        if (ar.succeeded()) {
          MessageConsumer<JsonObject> consumer = ar.result();
          consumer.handler(message -> {
            JsonObject transactionRequest = message.body();
            simulatePaymentTransaction(transactionRequest, message);
          });
          future.complete();
        } else {
          future.fail(ar.cause());
        }
      });
    return future;
  }

  private void simulatePaymentTransaction(JsonObject request, Message<JsonObject> sender) {
    // get current payment id
    long paymentId = request.getLong("payRawCounter");
    if (paymentId <= 0) {
      sender.fail(5111, "Negative payment id");
    }
    // generate payment data object
    Payment payment = new Payment(paymentId, request.getDouble("payAmount"),
      request.getInteger("paySource").shortValue());
    // do some transactions...

    // ThirdPaymentAPI.startTransactions(...);
    // Note: In production we should get payment id and payment time here

    // write into persistence backend
    pqs.addPaymentRecord(payment, ar -> {
      JsonObject replyMessage = new JsonObject().put("payId", paymentId)
        .put("paymentTime", payment.getPaymentTime());

      if (ar.succeeded()) {
        savePaymentLog(replyMessage, true);
        // reply to shopping endpoint
        sender.reply(replyMessage);
      } else {
        savePaymentLog(new JsonObject().put("error", "payment_record_fail")
          .mergeIn(replyMessage), false);
        sender.fail(5011, "payment_record_fail");
      }
    });
  }

  private Future<Void> initPaymentPersistence(PaymentQueryService pqs) {
    Future<Void> future = Future.future();
    pqs.initializePersistence(future.completer());
    return future;
  }

  private void savePaymentLog(JsonObject paymentData, boolean succeeded) {
    publishLogEvent("payment", paymentData, succeeded);
  }
}

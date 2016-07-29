package io.vertx.blueprint.microservice.payment;

import io.vertx.blueprint.microservice.common.BaseMicroserviceVerticle;
import io.vertx.blueprint.microservice.payment.impl.PaymentQueryServiceImpl;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ProxyHelper;

/**
 * A verticle that simulates payment processing.
 */
public class SimulatePaymentProcessingVerticle extends BaseMicroserviceVerticle {

  private static final String PAYMENT_TRANSACTION_ADDRESS = "events.service.payment.transactions";

  private PaymentQueryService pqs;

  @Override
  public void start(Future<Void> future) throws Exception {
    super.start(future);

    pqs = new PaymentQueryServiceImpl(vertx, config());
    ProxyHelper.registerService(PaymentQueryService.class, vertx, pqs, PaymentQueryService.SERVICE_ADDRESS);

    initPaymentPersistence(pqs)
      .compose(persistenceOkay -> publishEventBusService(PaymentQueryService.SERVICE_NAME,
        PaymentQueryService.SERVICE_ADDRESS, PaymentQueryService.class))
      .setHandler(ar -> {
        if (ar.succeeded()) {
          receiveAndProcess();
          future.complete();
        } else {
          future.fail(ar.cause());
        }
      });
  }

  private void receiveAndProcess() {
    vertx.eventBus().<JsonObject>consumer(PAYMENT_TRANSACTION_ADDRESS, message -> {
      JsonObject transactionRequest = message.body();
      simulatePaymentTransaction(transactionRequest, message);
    });
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

    // write into persistence backend
    pqs.addPaymentRecord(payment, ar -> {
      if (ar.succeeded()) {
        // reply to shopping endpoint
        sender.reply(new JsonObject().put("payId", paymentId)
          .put("paymentTime", payment.getPaymentTime()));
      } else {
        sender.fail(5011, "fail_to_record_payment");
      }
    });
  }

  private Future<Void> initPaymentPersistence(PaymentQueryService pqs) {
    Future<Void> future = Future.future();
    pqs.initializePersistence(future.completer());
    return future;
  }
}

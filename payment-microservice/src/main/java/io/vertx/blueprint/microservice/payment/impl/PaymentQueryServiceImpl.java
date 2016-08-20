package io.vertx.blueprint.microservice.payment.impl;

import io.vertx.blueprint.microservice.payment.Payment;
import io.vertx.blueprint.microservice.payment.PaymentQueryService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;

import java.util.List;

/**
 * Implementation of {@link PaymentQueryService}.
 */
public class PaymentQueryServiceImpl implements PaymentQueryService {

  private static final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS `payment` (\n" +
    "  `payId` varchar(24) NOT NULL,\n" +
    "  `payAmount` double NOT NULL,\n" +
    "  `paySource` smallint(6) NOT NULL,\n" +
    "  `paymentTime` bigint(20) NOT NULL,\n" +
    "  PRIMARY KEY (`payId`) )";
  private static final String INSERT_STATEMENT = "INSERT INTO payment (payId, payAmount, paySource, paymentTime) VALUES (?, ?, ?, ?)";
  private static final String FETCH_STATEMENT = "SELECT * FROM payment WHERE payId = ?";

  private final JDBCClient jdbc;

  public PaymentQueryServiceImpl(Vertx vertx, JsonObject config) {
    this.jdbc = JDBCClient.createNonShared(vertx, config);
  }

  @Override
  public void initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
    jdbc.getConnection(connHandler(resultHandler, connection -> {
      connection.execute(CREATE_STATEMENT, r -> {
        resultHandler.handle(r);
        connection.close();
      });
    }));
  }

  @Override
  public void addPaymentRecord(Payment payment, Handler<AsyncResult<Void>> resultHandler) {
    jdbc.getConnection(connHandler(resultHandler, connection -> {
      connection.updateWithParams(INSERT_STATEMENT, new JsonArray().add(payment.getPayId())
        .add(payment.getPayAmount())
        .add(payment.getPaySource())
        .add(payment.getPaymentTime()), r -> {
        if (r.succeeded()) {
          resultHandler.handle(Future.succeededFuture());
        } else {
          resultHandler.handle(Future.failedFuture(r.cause()));
        }
        connection.close();
      });
    }));
  }

  @Override
  public void retrievePaymentRecord(String payId, Handler<AsyncResult<Payment>> resultHandler) {
    jdbc.getConnection(connHandler(resultHandler, connection -> {
      connection.queryWithParams(FETCH_STATEMENT, new JsonArray().add(payId), r -> {
        if (r.succeeded()) {
          List<JsonObject> resList = r.result().getRows();
          if (resList == null || resList.isEmpty()) {
            resultHandler.handle(Future.succeededFuture());
          } else {
            resultHandler.handle(Future.succeededFuture(new Payment(resList.get(0))));
          }
        } else {
          resultHandler.handle(Future.failedFuture(r.cause()));
        }
        connection.close();
      });
    }));
  }

  /**
   * A helper methods that generates async handler for SQLConnection
   *
   * @return generated handler
   */
  private <T> Handler<AsyncResult<SQLConnection>> connHandler(Handler<AsyncResult<T>> h1, Handler<SQLConnection> h2) {
    return conn -> {
      if (conn.succeeded()) {
        final SQLConnection connection = conn.result();
        h2.handle(connection);
      } else {
        h1.handle(Future.failedFuture(conn.cause()));
      }
    };
  }

}

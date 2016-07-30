package io.vertx.blueprint.microservice.account.impl;

import io.vertx.blueprint.microservice.account.Account;
import io.vertx.blueprint.microservice.account.AccountService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;

import java.util.List;
import java.util.stream.Collectors;

/**
 * JDBC implementation of {@link AccountService}.
 *
 * @author Eric Zhao
 */
public class JdbcAccountServiceImpl implements AccountService {

  private static final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS `user_account` (\n" +
    "  `id` varchar(30) NOT NULL,\n" +
    "  `username` varchar(20) NOT NULL,\n" +
    "  `phone` varchar(20) NOT NULL,\n" +
    "  `email` varchar(45) NOT NULL,\n" +
    "  `birthDate` date NOT NULL,\n" +
    "  PRIMARY KEY (`id`),\n" +
    "  UNIQUE KEY `username_UNIQUE` (`username`) )";
  private static final String INSERT_STATEMENT = "INSERT INTO user_account (id, username, phone, email, birthDate) VALUES (?, ?, ?, ?, ?)";
  private static final String FETCH_STATEMENT = "SELECT * FROM user_account WHERE id = ?";
  private static final String FETCH_ALL_STATEMENT = "SELECT * FROM user_account";
  private static final String DELETE_STATEMENT = "DELETE FROM user_account WHERE id = ?";
  private static final String DELETE_ALL_STATEMENT = "DELETE FROM user_account";

  private final JDBCClient jdbc;

  public JdbcAccountServiceImpl(Vertx vertx, JsonObject config) {
    this.jdbc = JDBCClient.createNonShared(vertx, config);
  }

  @Override
  public AccountService initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
    jdbc.getConnection(connHandler(resultHandler, connection -> {
      connection.execute(CREATE_STATEMENT, r -> {
        resultHandler.handle(r);
        connection.close();
      });
    }));
    return this;
  }

  @Override
  public AccountService addAccount(Account account, Handler<AsyncResult<Void>> resultHandler) {
    jdbc.getConnection(connHandler(resultHandler, connection -> {
      connection.updateWithParams(INSERT_STATEMENT, new JsonArray().add(account.getId())
        .add(account.getUsername())
        .add(account.getPhone())
        .add(account.getEmail())
        .add(account.getBirthDate()), r -> {
        if (r.succeeded()) {
          resultHandler.handle(Future.succeededFuture());
        } else {
          resultHandler.handle(Future.failedFuture(r.cause()));
        }
        connection.close();
      });
    }));
    return this;
  }

  @Override
  public AccountService retrieveAccount(String id, Handler<AsyncResult<Account>> resultHandler) {
    jdbc.getConnection(connHandler(resultHandler, connection -> {
      connection.queryWithParams(FETCH_STATEMENT, new JsonArray().add(id), r -> {
        if (r.succeeded()) {
          List<JsonObject> resList = r.result().getRows();
          if (resList == null || resList.isEmpty()) {
            resultHandler.handle(Future.succeededFuture());
          } else {
            resultHandler.handle(Future.succeededFuture(new Account(resList.get(0))));
          }
        } else {
          resultHandler.handle(Future.failedFuture(r.cause()));
        }
        connection.close();
      });
    }));
    return this;
  }

  @Override
  public AccountService retrieveAllAccounts(Handler<AsyncResult<List<Account>>> resultHandler) {
    jdbc.getConnection(connHandler(resultHandler, connection -> {
      connection.query(FETCH_ALL_STATEMENT, r -> {
        if (r.succeeded()) {
          List<Account> resList = r.result().getRows().stream()
            .map(Account::new)
            .collect(Collectors.toList());
          resultHandler.handle(Future.succeededFuture(resList));
        } else {
          resultHandler.handle(Future.failedFuture(r.cause()));
        }
        connection.close();
      });
    }));
    return this;
  }

  @Override
  public AccountService deleteAccount(String id, Handler<AsyncResult<Void>> resultHandler) {
    jdbc.getConnection(connHandler(resultHandler, connection -> {
      JsonArray params = new JsonArray().add(id);
      connection.updateWithParams(DELETE_STATEMENT, params, r -> {
        if (r.succeeded()) {
          resultHandler.handle(Future.succeededFuture());
        } else {
          resultHandler.handle(Future.failedFuture(r.cause()));
        }
        connection.close();
      });
    }));
    return this;
  }

  @Override
  public AccountService deleteAllAccounts(Handler<AsyncResult<Void>> resultHandler) {
    jdbc.getConnection(connHandler(resultHandler, connection -> {
      connection.update(DELETE_ALL_STATEMENT, r -> {
        if (r.succeeded()) {
          resultHandler.handle(Future.succeededFuture());
        } else {
          resultHandler.handle(Future.failedFuture(r.cause()));
        }
        connection.close();
      });
    }));
    return this;
  }

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

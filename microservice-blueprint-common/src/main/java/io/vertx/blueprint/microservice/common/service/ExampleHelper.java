package io.vertx.blueprint.microservice.common.service;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Example helper class. Executes SQL statement.
 */
public class ExampleHelper {
  public static void initData(Vertx vertx, JsonObject config) {
    vertx.executeBlocking(future -> {
      try {
        Connection connection = DriverManager.getConnection(config.getString("url"),
          config.getString("user"), config.getString("password"));
        connection.createStatement().executeUpdate("INSERT INTO product (`productId`, `sellerId`, `name`, `price`, `illustration`, `type`) VALUES ('BK-9780134092669', 'TESTSE01', 'Computer Systems: A Programmer\\'s Perspective (3rd Edition)', '156.94', 'This book (CS:APP3e) is the third edition of a book that stems from the introductory computer systems course we developed at Carnegie Mellon University, starting in the Fall of 1998, called Introduction to Computer Systems (ICS).', 'Book') ON DUPLICATE KEY UPDATE productId = productId");
        connection.createStatement().executeUpdate("INSERT INTO product (`productId`, `sellerId`, `name`, `price`, `illustration`, `type`) VALUES ('A1763817', '112513', 'Fresh apple', '1.99', 'Fresh fruits!', 'Food') ON DUPLICATE KEY UPDATE productId = productId");
        connection.createStatement().executeUpdate("INSERT INTO product (`productId`, `sellerId`, `name`, `price`, `illustration`, `type`) VALUES ('BK-9780262033848', 'TESTSE01', 'Introduction to Algorithms, 3rd Edition', '66.32', 'Some books on algorithms are rigorous but incomplete; others cover masses of material but lack rigor. Introduction to Algorithms uniquely combines rigor and comprehensiveness. The book covers a broad range of algorithms in depth, yet makes their design and analysis accessible to all levels of readers. Each chapter is relatively self-contained and can be used as a unit of study. The algorithms are described in English and in a pseudocode designed to be readable by anyone who has done a little programming. The explanations have been kept elementary without sacrificing depth of coverage or mathematical rigor.', 'Book')  ON DUPLICATE KEY UPDATE productId = productId");
        future.complete();
      } catch (SQLException ex) {
        ex.printStackTrace();
        future.fail(ex);
      }
    }, ar -> {
      if (ar.failed()) {
        ar.cause().printStackTrace();
      }
    });
  }
}

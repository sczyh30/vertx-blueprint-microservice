package io.vertx.blueprint.microservice.inventory;

import io.restassured.response.Response;
import io.vertx.core.Vertx;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import redis.embedded.RedisServer;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.*;
import static com.jayway.awaitility.Awaitility.await;
import static org.hamcrest.core.Is.is;

/**
 * Test case for {@link InventoryRestAPIVerticle}.
 *
 * @author Eric Zhao
 */
public class InventoryApiTest {

  private static RedisServer server;

  private Vertx vertx;

  @BeforeClass
  static public void startRedis() throws Exception {
    server = new RedisServer(6379);
    System.out.println("Created embedded redis server on port 6379");
    server.start();
  }

  @AfterClass
  static public void stopRedis() throws Exception {
    server.stop();
  }

  @Before
  public void setUp() throws Exception {
    vertx = Vertx.vertx();
    AtomicBoolean completed = new AtomicBoolean();
    vertx.deployVerticle(new InventoryRestAPIVerticle(), ar -> completed.set(ar.succeeded()));
    await().untilAtomic(completed, is(true));
  }

  @After
  public void tearDown() throws Exception {
    AtomicBoolean completed = new AtomicBoolean();
    vertx.close((v) -> completed.set(true));
    await().untilAtomic(completed, is(true));
  }

  @Test
  public void testGetAndAdd() throws Exception {
    int productId = ThreadLocalRandom.current().nextInt();
    Response response = given().port(8086).get("/" + productId);
    assertThat(response.getStatusCode()).isEqualTo(200);
    assertThat(response.asString()).isEqualTo("0");

    int inc = 10;
    response = given().port(8086).put("/" + productId + "/increase?n=" + inc);
    assertThat(response.getStatusCode()).isEqualTo(200);
    assertThat(response.asString()).isEqualTo(String.valueOf(inc));

    int dec = 8;
    response = given().port(8086).put("/" + productId + "/decrease?n=" + dec);
    assertThat(response.getStatusCode()).isEqualTo(200);
    assertThat(response.asString()).isEqualTo(String.valueOf(inc - dec));
  }

}
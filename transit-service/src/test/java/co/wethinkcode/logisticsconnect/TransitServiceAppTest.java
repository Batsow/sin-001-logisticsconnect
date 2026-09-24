package co.wethinkcode.logisticsconnect;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransitServiceAppTest {

    @Test
    void shouldReturnEtaForHub() {
        Javalin app = TransitServiceApp.createApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/eta/H-500");

            assertEquals(200, response.code());
        });
    }


    @Test
    void shouldReturnEtaAsJson() {
        Javalin app = TransitServiceApp.createApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/eta/H-500");

            assertEquals(200, response.code());
            assertTrue(response.header("Content-Type").contains("application/json"));
            assertTrue(response.body().string().contains("\"hubId\":\"H-500\""));
        });
    }
}

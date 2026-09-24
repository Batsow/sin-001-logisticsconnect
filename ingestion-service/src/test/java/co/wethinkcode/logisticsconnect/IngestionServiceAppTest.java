package co.wethinkcode.logisticsconnect;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IngestionServiceAppTest {

    @Test
    void shouldReturnOkFromHealthEndpoint() {
        Javalin app = IngestionServiceApp.createApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/health");

            assertEquals(200, response.code());
            assertEquals("OK", response.body().string());
        });
    }


    @Test
    void shouldReturnHubsFromEndpoint() {
        Javalin app = IngestionServiceApp.createApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/hubs");

            assertEquals(200, response.code());
            assertEquals("[", response.body().string().substring(0, 1));
        });
    }


    @Test
    void shouldReturnCleanedHubData() {
        Javalin app = IngestionServiceApp.createApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/hubs");

            assertEquals(200, response.code());
            assertTrue(response.body().string().contains("H-500"));
        });
    }
}

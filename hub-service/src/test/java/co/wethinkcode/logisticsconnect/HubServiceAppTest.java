package co.wethinkcode.logisticsconnect;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HubServiceAppTest {
    @Test
    void shouldReturnHubDetails() {
        Javalin app = HubServiceApp.createApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/hubs/H-500");

            assertEquals(200, response.code());
        });
    }


    @Test
    void shouldReturnHubIdInResponse() {
        Javalin app = HubServiceApp.createApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/hubs/H-500");

            assertEquals(200, response.code());
            assertTrue(response.body().string().contains("H-500"));
        });
    }


    @Test
    void shouldReturnSortingCenterInResponse() {
        Javalin app = HubServiceApp.createApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/hubs/H-500");

            assertEquals(200, response.code());
            assertTrue(response.body().string().contains("Johannesburg Central"));
        });
    }
}

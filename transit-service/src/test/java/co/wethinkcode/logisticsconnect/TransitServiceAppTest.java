package co.wethinkcode.logisticsconnect;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransitServiceAppTest {

    private Javalin hubService;
    private String hubServiceUrl;
    private AtomicBoolean hubServiceWasCalled;

    @BeforeEach
    void startFakeHubService() {
        hubServiceWasCalled = new AtomicBoolean(false);

        hubService = Javalin.create();

        hubService.get("/hubs/{hubId}", ctx -> {
            hubServiceWasCalled.set(true);

            ctx.json(Map.of(
                    "hubId", "H-500",
                    "province", "Gauteng",
                    "sortingCenter", "Johannesburg Central",
                    "active", true
            ));
        });

        hubService.start(0);

        hubServiceUrl = "http://localhost:" + hubService.port();
    }

    @AfterEach
    void stopFakeHubService() {
        hubService.stop();
    }

    @Test
    void shouldReturnEtaForHub() {
        Javalin app = TransitServiceApp.createApp(hubServiceUrl);

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/eta/H-500");

            assertEquals(200, response.code());
        });
    }

    @Test
    void shouldReturnEtaAsJson() {
        Javalin app = TransitServiceApp.createApp(hubServiceUrl);

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/eta/H-500");

            assertEquals(200, response.code());
            assertTrue(response.header("Content-Type").contains("application/json"));

            String body = response.body().string();

            assertTrue(body.contains("\"hubId\":\"H-500\""));
        });
    }

    @Test
    void shouldGetHubInformationFromHubService() {
        Javalin app = TransitServiceApp.createApp(hubServiceUrl);

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/eta/H-500");

            assertEquals(200, response.code());
            assertTrue(hubServiceWasCalled.get());
        });
    }
}
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
    private Javalin delayStageService;

    private String hubServiceUrl;
    private String delayStageServiceUrl;

    private AtomicBoolean hubServiceWasCalled;
    private AtomicBoolean delayStageServiceWasCalled;

    @BeforeEach
    void startFakeServices() {
        hubServiceWasCalled = new AtomicBoolean(false);
        delayStageServiceWasCalled = new AtomicBoolean(false);

        // Fake hub-service
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

        // Fake delay-stage-service
        delayStageService = Javalin.create();

        delayStageService.get("/delay-stage/{hubId}", ctx -> {
            delayStageServiceWasCalled.set(true);

            ctx.json(Map.of(
                    "hubId", "H-500",
                    "stage", 3
            ));
        });

        delayStageService.start(0);
        delayStageServiceUrl = "http://localhost:" + delayStageService.port();
    }

    @AfterEach
    void stopFakeServices() {
        hubService.stop();
        delayStageService.stop();
    }

    @Test
    void shouldReturnEtaForHub() {
        Javalin app = TransitServiceApp.createApp(
                hubServiceUrl,
                delayStageServiceUrl
        );

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/eta/H-500");

            assertEquals(200, response.code());
        });
    }

    @Test
    void shouldReturnEtaAsJson() {
        Javalin app = TransitServiceApp.createApp(
                hubServiceUrl,
                delayStageServiceUrl
        );

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
        Javalin app = TransitServiceApp.createApp(
                hubServiceUrl,
                delayStageServiceUrl
        );

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/eta/H-500");

            assertEquals(200, response.code());
            assertTrue(hubServiceWasCalled.get());
        });
    }

    @Test
    void shouldIncludeSortingCenterInEtaResponse() {
        Javalin app = TransitServiceApp.createApp(
                hubServiceUrl,
                delayStageServiceUrl
        );

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/eta/H-500");

            assertEquals(200, response.code());

            String body = response.body().string();

            assertTrue(
                    body.contains("\"sortingCenter\":\"Johannesburg Central\"")
            );
        });
    }

    @Test
    void shouldGetDelayStageFromDelayStageService() {
        Javalin app = TransitServiceApp.createApp(
                hubServiceUrl,
                delayStageServiceUrl
        );

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/eta/H-500");

            assertEquals(200, response.code());
            assertTrue(delayStageServiceWasCalled.get());
        });
    }


    @Test
    void shouldCalculateEtaUsingDelayStage() {
        Javalin app = TransitServiceApp.createApp(
                hubServiceUrl,
                delayStageServiceUrl
        );

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/eta/H-500");

            assertEquals(200, response.code());

            String body = response.body().string();

            assertTrue(body.contains("\"delayStage\":3"));
            assertTrue(body.contains("\"etaMinutes\":60"));
        });
    }
}
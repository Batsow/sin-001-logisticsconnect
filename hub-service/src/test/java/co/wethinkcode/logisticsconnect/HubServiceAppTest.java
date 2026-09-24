package co.wethinkcode.logisticsconnect;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HubServiceAppTest {

    private Javalin ingestionApp;
    private AtomicBoolean ingestionServiceWasCalled;

    @BeforeEach
    void startFakeIngestionService() {
        ingestionServiceWasCalled = new AtomicBoolean(false);

        ingestionApp = Javalin.create();

        ingestionApp.get("/hubs", ctx -> {
            ingestionServiceWasCalled.set(true);

            ctx.json(List.of(
                    Map.of(
                            "hubId", "H-500",
                            "province", "Gauteng",
                            "sortingCenter", "Johannesburg Central",
                            "active", true
                    )
            ));
        });

        ingestionApp.start(7050);
    }

    @AfterEach
    void stopFakeIngestionService() {
        ingestionApp.stop();
    }

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

    @Test
    void shouldGetHubDataFromIngestionService() {
        Javalin app = HubServiceApp.createApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/hubs/H-500");

            assertEquals(200, response.code());
            assertTrue(response.body().string().contains("Johannesburg Central"));
            assertTrue(ingestionServiceWasCalled.get());
        });
    }


    @Test
    void shouldReturnJsonResponse() {
        Javalin app = HubServiceApp.createApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/hubs/H-500");

            assertEquals(200, response.code());
            assertTrue(response.header("Content-Type").contains("application"));
        });
    }


    @Test
    void shouldReturnProvinceInResponse() {
        Javalin app = HubServiceApp.createApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/hubs/H-500");

            assertEquals(200, response.code());
            assertTrue(response.body().string().contains("Gauteng"));
        });
    }


    @Test
    void shouldReturnActiveStatusInResponse() {
        Javalin app = HubServiceApp.createApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/hubs/H-500");

            assertEquals(200, response.code());
            assertTrue(response.body().string().contains("\"active\":true"));
        });
    }



    @Test
    void shouldReturn404ForUnknownHub() {
        Javalin app = HubServiceApp.createApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/hubs/H-999");

            assertEquals(404, response.code());
        });
    }
}
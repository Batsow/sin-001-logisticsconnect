package co.wethinkcode.logisticsconnect;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.Test;

import java.util.List;

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


    @Test
    void shouldReturnCleanedHubValues() {
        Javalin app = IngestionServiceApp.createApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/hubs");
            String body = response.body().string();

            assertTrue(body.contains("\"hubId\":\"H-500\""));
            assertTrue(body.contains("\"province\":\"Gauteng\""));
            assertTrue(body.contains("\"sortingCenter\":\"Johannesburg Central\""));
            assertTrue(body.contains("\"active\":true"));
        });
    }



    @Test
    void shouldReturnElevenUniqueHubs() {
        Javalin app = IngestionServiceApp.createApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/hubs");

            ObjectMapper mapper = new ObjectMapper();

            List<?> hubs = mapper.readValue(
                    response.body().string(),
                    List.class
            );

            assertEquals(11, hubs.size());
        });
    }
}

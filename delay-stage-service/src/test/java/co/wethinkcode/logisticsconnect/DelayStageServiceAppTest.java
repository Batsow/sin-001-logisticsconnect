package co.wethinkcode.logisticsconnect;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DelayStageServiceAppTest {
    @Test
    void shouldReturnDelayStage() {
        Javalin app = DelayStageServiceApp.createApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/delay-stage/H-500");

            assertEquals(200, response.code());
        });
    }


    @Test
    void shouldReturnCurrentDelayStage() {
        Javalin app = DelayStageServiceApp.createApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/delay-stage/H-500");

            assertEquals(200, response.code());
            assertTrue(response.body().string().contains("0"));
        });
    }


    @Test
    void shouldReturnDelayStageAsJson() {
        Javalin app = DelayStageServiceApp.createApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/delay-stage/H-500");

            String body = response.body().string();

            assertEquals(200, response.code());
            assertTrue(response.header("Content-Type").contains("application/json"));
            assertTrue(body.contains("\"stage\":0"));
        });
    }


    @Test
    void shouldRejectInvalidDelayStage() {
        Javalin app = DelayStageServiceApp.createApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.put("/delay-stage/H-500?stage=9");

            assertEquals(400, response.code());
        });
    }


    @Test
    void shouldChangeDelayStage() {
        Javalin app = DelayStageServiceApp.createApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.put("/delay-stage/H-500?stage=3");

            assertEquals(200, response.code());

            var getResponse = client.get("/delay-stage/H-500");

            assertEquals(200, getResponse.code());
            assertTrue(getResponse.body().string().contains("\"stage\":3"));
        });
    }
}

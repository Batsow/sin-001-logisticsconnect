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

            assertEquals(200, response.code());
            assertTrue(response.header("Content-Type").contains("application/json"));
            assertTrue(response.body().string().contains("\"stage\":0"));
        });
    }
}

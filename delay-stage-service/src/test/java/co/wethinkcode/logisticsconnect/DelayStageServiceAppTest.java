package co.wethinkcode.logisticsconnect;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DelayStageServiceAppTest {
    @Test
    void shouldReturnDelayStage() {
        Javalin app = DelayStageServiceApp.createApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/delay-stage/H-500");

            assertEquals(200, response.code());
        });
    }
}

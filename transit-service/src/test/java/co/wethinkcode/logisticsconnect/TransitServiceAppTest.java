package co.wethinkcode.logisticsconnect;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransitServiceAppTest {

    @Test
    void shouldReturnEtaForHub() {
        Javalin app = TransitServiceApp.createApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/eta/H-500");

            assertEquals(200, response.code());
        });
    }
}

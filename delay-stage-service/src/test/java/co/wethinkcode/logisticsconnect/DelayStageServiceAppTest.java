package co.wethinkcode.logisticsconnect;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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


    @Test
    void shouldRejectMissingDelayStage() {
        Javalin app = DelayStageServiceApp.createApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.put("/delay-stage/H-500");

            assertEquals(400, response.code());
        });
    }


    @Test
    void shouldRejectNonNumericDelayStage() {
        Javalin app = DelayStageServiceApp.createApp();

        JavalinTest.test(app, (server, client) -> {
            var response = client.put("/delay-stage/H-500?stage=abc");

            assertEquals(400, response.code());
        });
    }


    @Test
    void shouldPublishWhenDelayStageChanges() {
        List<String> publishedMessages = new ArrayList<>();

        DelayStagePublisher publisher = (hubId, stage) ->
                publishedMessages.add(hubId + ":" + stage);

        Javalin app = DelayStageServiceApp.createApp(publisher);

        JavalinTest.test(app, (server, client) -> {
            var response = client.put("/delay-stage/H-500?stage=3");

            assertEquals(200, response.code());
            assertTrue(publishedMessages.contains("H-500:3"));
        });
    }


    @Test
    void shouldNotPublishWhenDelayStageDoesNotChange() {
        List<String> publishedMessages = new ArrayList<>();

        DelayStagePublisher publisher = (hubId, stage) ->
                publishedMessages.add(hubId + ":" + stage);

        Javalin app = DelayStageServiceApp.createApp(publisher);

        JavalinTest.test(app, (server, client) -> {
            var firstResponse = client.put("/delay-stage/H-500?stage=3");
            assertEquals(200, firstResponse.code());

            var secondResponse = client.put("/delay-stage/H-500?stage=3");
            assertEquals(200, secondResponse.code());

            assertEquals(1, publishedMessages.size());
            assertTrue(publishedMessages.contains("H-500:3"));
        });
    }


    @Test
    void shouldPublishHubIdAndNewStage() {
        List<String> publishedMessages = new ArrayList<>();

        DelayStagePublisher publisher = (hubId, stage) ->
                publishedMessages.add(hubId + ":" + stage);

        Javalin app = DelayStageServiceApp.createApp(publisher);

        JavalinTest.test(app, (server, client) -> {
            var response = client.put("/delay-stage/H-501?stage=5");

            assertEquals(200, response.code());
            assertTrue(publishedMessages.contains("H-501:5"));
        });
    }
}




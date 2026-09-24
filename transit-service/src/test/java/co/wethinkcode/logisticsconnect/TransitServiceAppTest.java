package co.wethinkcode.logisticsconnect;

import co.wethinkcode.logisticsconnect.mq.MqConfig;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

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

            String hubId = ctx.pathParam("hubId");

            if (!hubId.equals("H-500")) {
                ctx.status(404);
                return;
            }

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


    @Test
    void shouldReturnNotFoundWhenHubDoesNotExist() {
        Javalin app = TransitServiceApp.createApp(
                hubServiceUrl,
                delayStageServiceUrl
        );

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/eta/H-999");

            assertEquals(404, response.code());
        });
    }


    @Test
    void shouldUseStoredDelayStageForEta() {
        TransitDelayStageStore store = new TransitDelayStageStore();

        store.updateStage("H-500", 5);

        Javalin app = TransitServiceApp.createApp(
                hubServiceUrl,
                store
        );

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/eta/H-500");

            assertEquals(200, response.code());

            String body = response.body().string();

            assertTrue(body.contains("\"delayStage\":5"));
            assertTrue(body.contains("\"etaMinutes\":80"));

            assertTrue(!delayStageServiceWasCalled.get());
        });
    }


    @Test
    void shouldUseDelayStageFromSubscriberStore() throws Exception {
        TransitDelayStageStore store = new TransitDelayStageStore();

        TransitDelayStageSubscriber subscriber =
                new TransitDelayStageSubscriber(store);

        subscriber.start();

        try {
            // Give the subscriber a moment to connect to ActiveMQ.
            Thread.sleep(200);

            // Simulate a delay-stage update arriving through the message handler.
            subscriber.handleMessage(
                    "{\"hubId\":\"H-500\",\"stage\":7}"
            );

            Javalin app = TransitServiceApp.createApp(
                    hubServiceUrl,
                    store
            );

            JavalinTest.test(app, (server, client) -> {
                var response = client.get("/eta/H-500");

                assertEquals(200, response.code());

                String body = response.body().string();

                assertTrue(body.contains("\"delayStage\":7"));
                assertTrue(body.contains("\"etaMinutes\":100"));

                assertTrue(!delayStageServiceWasCalled.get());
            });
        } finally {
            subscriber.stop();
        }
    }

    @Test
    void shouldUseDelayStageReceivedFromActiveMqForEta() throws Exception {
        TransitDelayStageStore store = new TransitDelayStageStore();

        TransitDelayStageSubscriber subscriber =
                new TransitDelayStageSubscriber(store);

        subscriber.start();

        try {
            ActiveMQConnectionFactory factory =
                    new ActiveMQConnectionFactory(MqConfig.BROKER_URL);

            Connection connection = factory.createConnection();

            Session session = connection.createSession(
                    false,
                    Session.AUTO_ACKNOWLEDGE
            );

            Topic topic = session.createTopic(MqConfig.TOPIC);

            MessageProducer producer = session.createProducer(topic);

            connection.start();

            TextMessage message = session.createTextMessage(
                    "{\"hubId\":\"H-500\",\"stage\":7}"
            );

            producer.send(message);

            // Give the subscriber time to receive the message.
            for (int i = 0; i < 20; i++) {
                if (store.getStage("H-500") == 7) {
                    break;
                }

                Thread.sleep(100);
            }

            Javalin app = TransitServiceApp.createApp(
                    hubServiceUrl,
                    store
            );

            JavalinTest.test(app, (server, client) -> {
                var response = client.get("/eta/H-500");

                assertEquals(200, response.code());

                String body = response.body().string();

                assertTrue(body.contains("\"delayStage\":7"));
                assertTrue(body.contains("\"etaMinutes\":100"));

                assertTrue(!delayStageServiceWasCalled.get());
            });

            producer.close();
            session.close();
            connection.close();

        } finally {
            subscriber.stop();
        }
    }
}
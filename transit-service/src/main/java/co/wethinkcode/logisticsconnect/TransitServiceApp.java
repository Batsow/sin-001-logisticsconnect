package co.wethinkcode.logisticsconnect;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class TransitServiceApp {

    public static Javalin createApp() {
        return createApp(
                "http://localhost:7051",
                "http://localhost:7052"
        );
    }

    public static Javalin createApp(String hubServiceUrl) {
        return createApp(
                hubServiceUrl,
                "http://localhost:7052"
        );
    }

    public static Javalin createApp(
            String hubServiceUrl,
            String delayStageServiceUrl
    ) {
        return createAppWithStore(
                hubServiceUrl,
                delayStageServiceUrl,
                null
        );
    }

    public static Javalin createApp(
            String hubServiceUrl,
            TransitDelayStageStore store
    ) {
        return createAppWithStore(
                hubServiceUrl,
                null,
                store
        );
    }

    private static Javalin createAppWithStore(
            String hubServiceUrl,
            String delayStageServiceUrl,
            TransitDelayStageStore store
    ) {
        Javalin app = Javalin.create();

        app.get("/health", ctx -> ctx.result("OK"));

        app.get("/eta/{hubId}", ctx -> {
            String hubId = ctx.pathParam("hubId");

            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();

            // Get hub information
            HttpRequest hubRequest = HttpRequest.newBuilder()
                    .uri(URI.create(hubServiceUrl + "/hubs/" + hubId))
                    .GET()
                    .build();

            HttpResponse<String> hubResponse = client.send(
                    hubRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (hubResponse.statusCode() == 404) {
                ctx.status(404);
                return;
            }

            Map<String, Object> hub = mapper.readValue(
                    hubResponse.body(),
                    new TypeReference<Map<String, Object>>() {}
            );

            int stage;

            if (store != null) {
                stage = store.getStage(hubId);
            } else {
                // Get current delay stage from delay-stage-service
                HttpRequest delayRequest = HttpRequest.newBuilder()
                        .uri(URI.create(
                                delayStageServiceUrl
                                        + "/delay-stage/"
                                        + hubId
                        ))
                        .GET()
                        .build();

                HttpResponse<String> delayResponse = client.send(
                        delayRequest,
                        HttpResponse.BodyHandlers.ofString()
                );

                Map<String, Object> delayStage = mapper.readValue(
                        delayResponse.body(),
                        new TypeReference<Map<String, Object>>() {}
                );

                stage = ((Number) delayStage.get("stage")).intValue();
            }

            int etaMinutes = 30 + (stage * 10);

            ctx.json(Map.of(
                    "hubId", hub.get("hubId"),
                    "sortingCenter", hub.get("sortingCenter"),
                    "delayStage", stage,
                    "etaMinutes", etaMinutes
            ));
        });

        return app;
    }

    public static void main(String[] args) throws Exception {
        TransitDelayStageStore store =
                new TransitDelayStageStore();

        TransitDelayStageSubscriber subscriber =
                new TransitDelayStageSubscriber(store);

        subscriber.start();

        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                    try {
                        subscriber.stop();
                    } catch (Exception ignored) {
                    }
                })
        );

        createApp(
                "http://localhost:7051",
                store
        ).start(7053);
    }
}
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
        Javalin app = Javalin.create();

        app.get("/health", ctx -> ctx.result("OK"));

        app.get("/eta/{hubId}", ctx -> {
            String hubId = ctx.pathParam("hubId");

            HttpClient client = HttpClient.newHttpClient();

            // Get hub information
            HttpRequest hubRequest = HttpRequest.newBuilder()
                    .uri(URI.create(hubServiceUrl + "/hubs/" + hubId))
                    .GET()
                    .build();

            HttpResponse<String> hubResponse = client.send(
                    hubRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> hub = mapper.readValue(
                    hubResponse.body(),
                    new TypeReference<Map<String, Object>>() {}
            );

            // Get current delay stage
            HttpRequest delayRequest = HttpRequest.newBuilder()
                    .uri(URI.create(delayStageServiceUrl + "/delay-stage/" + hubId))
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

            ctx.json(Map.of(
                    "hubId", hub.get("hubId"),
                    "sortingCenter", hub.get("sortingCenter"),
                    "delayStage", delayStage.get("stage"),
                    "etaMinutes", 0
            ));
        });

        return app;
    }

    public static void main(String[] args) {
        createApp().start(7053);
    }
}
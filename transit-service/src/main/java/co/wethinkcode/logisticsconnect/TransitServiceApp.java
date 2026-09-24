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
        return createApp("http://localhost:7051");
    }

    public static Javalin createApp(String hubServiceUrl) {
        Javalin app = Javalin.create();

        app.get("/health", ctx -> ctx.result("OK"));

        app.get("/eta/{hubId}", ctx -> {
            String hubId = ctx.pathParam("hubId");

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(hubServiceUrl + "/hubs/" + hubId))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> hub = mapper.readValue(
                    response.body(),
                    new TypeReference<Map<String, Object>>() {}
            );

            ctx.json(Map.of(
                    "hubId", hub.get("hubId"),
                    "etaMinutes", 0
            ));
        });

        return app;
    }

    public static void main(String[] args) {
        createApp().start(7053);
    }
}
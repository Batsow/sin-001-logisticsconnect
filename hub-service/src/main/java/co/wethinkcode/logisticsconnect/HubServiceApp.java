package co.wethinkcode.logisticsconnect;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class HubServiceApp {

    public static Javalin createApp() {
        Javalin app = Javalin.create();

        app.get("/health", ctx -> ctx.result("OK"));

        app.get("/hubs/{hubId}", ctx -> {
            String hubId = ctx.pathParam("hubId");

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:7050/hubs"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            ObjectMapper mapper = new ObjectMapper();

            List<Map<String, Object>> hubs = mapper.readValue(
                    response.body(),
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            for (Map<String, Object> hub : hubs) {
                if (hubId.equals(hub.get("hubId"))) {
                    String province = (String) hub.get("province");
                    String sortingCenter = (String) hub.get("sortingCenter");

                    ctx.json(Map.of(
                            "hubId", hubId,
                            "province", province,
                            "sortingCenter", sortingCenter
                    ));
                    return;
                }
            }

            ctx.status(404);
        });
        return app;
    }

    public static void main(String[] args) {
        createApp().start(7051);
    }
}

package co.wethinkcode.logisticsconnect;

import io.javalin.Javalin;

import java.util.Map;

public class TransitServiceApp {

    public static Javalin createApp() {
        Javalin app = Javalin.create();

        app.get("/health", ctx -> ctx.result("OK"));

        app.get("/eta/{hubId}", ctx -> {
            String hubId = ctx.pathParam("hubId");

            ctx.json(Map.of(
                    "hubId", hubId,
                    "etaMinutes", 0
            ));
        });

        return app;
    }

    public static void main(String[] args) {
        createApp().start(7053);
    }
}
package co.wethinkcode.logisticsconnect;

import io.javalin.Javalin;

import java.util.ArrayList;
import java.util.List;

public class IngestionServiceApp {

    public static Javalin createApp() {
        Javalin app = Javalin.create();

        app.get("/health", ctx -> ctx.result("OK"));

        app.get("/hubs", ctx -> {
            HubCsvReader reader = new HubCsvReader();
            List<Hub> hubs = reader.read("src/main/resources/hubs-global.csv");

            ctx.json(hubs);
        });

        return app;
    }

    public static void main(String[] args) {
        createApp().start(7050);
    }
}

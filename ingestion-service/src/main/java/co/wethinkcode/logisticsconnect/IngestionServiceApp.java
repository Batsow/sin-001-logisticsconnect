package co.wethinkcode.logisticsconnect;

import io.javalin.Javalin;

public class IngestionServiceApp {

    public static Javalin createApp() {
        Javalin app = Javalin.create();

        app.get("/health", ctx -> ctx.result("OK"));

        return app;
    }

    public static void main(String[] args) {
        createApp().start(7050);
    }
}

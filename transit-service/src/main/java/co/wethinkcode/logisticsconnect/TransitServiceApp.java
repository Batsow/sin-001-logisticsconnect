package co.wethinkcode.logisticsconnect;

import io.javalin.Javalin;

public class TransitServiceApp {

    public static Javalin createApp() {
        Javalin app = Javalin.create();

        app.get("/health", ctx -> ctx.result("OK"));

        app.get("/eta/{hubId}", ctx -> ctx.status(200));

        return app;
    }

    public static void main(String[] args) {
        createApp().start(7053);
    }
}
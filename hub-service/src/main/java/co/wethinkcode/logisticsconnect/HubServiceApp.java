package co.wethinkcode.logisticsconnect;

import io.javalin.Javalin;

public class HubServiceApp {

    public static Javalin createApp() {
        Javalin app = Javalin.create();

        app.get("/health", ctx -> ctx.result("OK"));

        app.get("/hubs/{hubId}", ctx -> ctx.result(ctx.pathParam("hubId")));

        return app;
    }

    public static void main(String[] args) {
        createApp().start(7051);
    }
}

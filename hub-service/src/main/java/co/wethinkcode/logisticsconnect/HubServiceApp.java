package co.wethinkcode.logisticsconnect;

import io.javalin.Javalin;

public class HubServiceApp {

    public static Javalin createApp() {
        Javalin app = Javalin.create();

        app.get("/health", ctx -> ctx.result("OK"));

        app.get("/hubs/{hubId}", ctx -> {
            String hubId = ctx.pathParam("hubId");

            if (hubId.equals("H-500")) {
                ctx.result("H-500 - Johannesburg Central");
            } else {
                ctx.result(hubId);
            }
        });
        return app;
    }

    public static void main(String[] args) {
        createApp().start(7051);
    }
}

package co.wethinkcode.logisticsconnect;

import io.javalin.Javalin;

import java.util.Map;

public class DelayStageServiceApp {

    public static Javalin createApp() {
        Javalin app = Javalin.create();

        app.get("/health", ctx -> ctx.result("OK"));

        app.get("/delay-stage/{hubId}", ctx -> {
            String hubId = ctx.pathParam("hubId");

            ctx.json(Map.of(
                    "hubId", hubId,
                    "stage", 0
            ));
        });
        return app;
    }

    public static void main(String[] args) {
        createApp().start(7052);
    }
}

// MQ TODO: publishes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.logisticsconnect.mq.MqConfig)

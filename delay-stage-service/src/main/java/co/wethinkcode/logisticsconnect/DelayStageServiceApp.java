package co.wethinkcode.logisticsconnect;

import io.javalin.Javalin;

import java.util.HashMap;
import java.util.Map;

public class DelayStageServiceApp {

    public static Javalin createApp() {
        return createApp((hubId, stage) -> {
        });
    }

    public static Javalin createApp(DelayStagePublisher publisher) {
        Javalin app = Javalin.create();

        Map<String, Integer> delayStages = new HashMap<>();

        app.get("/health", ctx -> ctx.result("OK"));

        app.get("/delay-stage/{hubId}", ctx -> {
            String hubId = ctx.pathParam("hubId");

            int stage = delayStages.getOrDefault(hubId, 0);

            ctx.json(Map.of(
                    "hubId", hubId,
                    "stage", stage
            ));
        });

        app.put("/delay-stage/{hubId}", ctx -> {
            String hubId = ctx.pathParam("hubId");
            String stageParameter = ctx.queryParam("stage");

            if (stageParameter == null) {
                ctx.status(400);
                return;
            }

            int stage;

            try {
                stage = Integer.parseInt(stageParameter);
            } catch (NumberFormatException e) {
                ctx.status(400);
                return;
            }

            if (stage < 0 || stage > 8) {
                ctx.status(400);
                return;
            }

            int previousStage = delayStages.getOrDefault(hubId, 0);

            delayStages.put(hubId, stage);

            if (previousStage != stage) {
                publisher.publish(hubId, stage);
            }

            ctx.status(200);
        });

        return app;
    }

    public static void main(String[] args) {
        createApp().start(7052);
    }
}
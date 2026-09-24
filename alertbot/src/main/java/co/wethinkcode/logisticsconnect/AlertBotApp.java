package co.wethinkcode.logisticsconnect;

import io.javalin.Javalin;

public class AlertBotApp {

    public static void main(String[] args)  throws Exception {
        AlertEvaluator evaluator =
                new AlertEvaluator(5);

        AlertNotifier notifier =
                new SimulatedAlertNotifier();

        AlertBotMessageHandler handler =
                new AlertBotMessageHandler(
                        evaluator,
                        notifier
                );

        AlertBotMessageSubscriber subscriber =
                new AlertBotMessageSubscriber(handler);

        subscriber.start();

        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                    try {
                        subscriber.stop();
                    } catch (Exception ignored) {
                    }
                })
        );

        Javalin app = Javalin.create();

        app.get("/health", ctx -> ctx.result("OK"));

        app.start(7054);
    }
}


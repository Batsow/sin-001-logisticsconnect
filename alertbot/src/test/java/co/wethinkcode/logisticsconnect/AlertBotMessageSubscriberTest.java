package co.wethinkcode.logisticsconnect;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlertBotMessageSubscriberTest {

    @Test
    void shouldPassMessageToAlertHandler() throws Exception {
        AlertEvaluator evaluator = new AlertEvaluator(5);

        List<String> alerts = new ArrayList<>();

        AlertNotifier notifier = (hubId, stage) ->
                alerts.add(hubId + ":" + stage);

        AlertBotMessageHandler handler =
                new AlertBotMessageHandler(evaluator, notifier);

        AlertBotMessageSubscriber subscriber =
                new AlertBotMessageSubscriber(handler);

        subscriber.handleMessage(
                "{\"hubId\":\"H-500\",\"stage\":7}"
        );

        assertEquals(
                List.of("H-500:7"),
                alerts
        );
    }
}
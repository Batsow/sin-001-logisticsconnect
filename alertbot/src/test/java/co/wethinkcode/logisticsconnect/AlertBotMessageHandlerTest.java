package co.wethinkcode.logisticsconnect;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlertBotMessageHandlerTest {

    @Test
    void shouldSendAlertWhenDelayStageReachesThreshold() throws Exception {
        AlertEvaluator evaluator = new AlertEvaluator(5);

        List<String> alerts = new ArrayList<>();

        AlertNotifier notifier = (hubId, stage) ->
                alerts.add(hubId + ":" + stage);

        AlertBotMessageHandler handler =
                new AlertBotMessageHandler(evaluator, notifier);

        handler.handleMessage(
                "{\"hubId\":\"H-500\",\"stage\":5}"
        );

        assertEquals(
                List.of("H-500:5"),
                alerts
        );
    }

    @Test
    void shouldNotSendAlertBelowThreshold() throws Exception {
        AlertEvaluator evaluator = new AlertEvaluator(5);

        List<String> alerts = new ArrayList<>();

        AlertNotifier notifier = (hubId, stage) ->
                alerts.add(hubId + ":" + stage);

        AlertBotMessageHandler handler =
                new AlertBotMessageHandler(evaluator, notifier);

        handler.handleMessage(
                "{\"hubId\":\"H-500\",\"stage\":4}"
        );

        assertEquals(
                List.of(),
                alerts
        );
    }

    @Test
    void shouldSendAlertForHighDelayStage() throws Exception {
        AlertEvaluator evaluator = new AlertEvaluator(5);

        List<String> alerts = new ArrayList<>();

        AlertNotifier notifier = (hubId, stage) ->
                alerts.add(hubId + ":" + stage);

        AlertBotMessageHandler handler =
                new AlertBotMessageHandler(evaluator, notifier);

        handler.handleMessage(
                "{\"hubId\":\"H-500\",\"stage\":8}"
        );

        assertEquals(
                List.of("H-500:8"),
                alerts
        );
    }
}
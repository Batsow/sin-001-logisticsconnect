package co.wethinkcode.logisticsconnect;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class AlertBotMessageHandler {

    private final AlertEvaluator evaluator;
    private final AlertNotifier notifier;
    private final ObjectMapper objectMapper;

    public AlertBotMessageHandler(
            AlertEvaluator evaluator,
            AlertNotifier notifier
    ) {
        this.evaluator = evaluator;
        this.notifier = notifier;
        this.objectMapper = new ObjectMapper();
    }

    public void handleMessage(String messageBody) throws Exception {
        Map<String, Object> message = objectMapper.readValue(
                messageBody,
                new TypeReference<Map<String, Object>>() {}
        );

        String hubId = (String) message.get("hubId");
        int stage = ((Number) message.get("stage")).intValue();

        if (evaluator.shouldAlert(stage)) {
            notifier.sendAlert(hubId, stage);
        }
    }
}
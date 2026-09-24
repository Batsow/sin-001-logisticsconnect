package co.wethinkcode.logisticsconnect;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class TransitDelayStageSubscriber {

    private final TransitDelayStageStore store;
    private final ObjectMapper objectMapper;

    public TransitDelayStageSubscriber(TransitDelayStageStore store) {
        this.store = store;
        this.objectMapper = new ObjectMapper();
    }

    public void handleMessage(String messageBody) throws Exception {
        Map<String, Object> message = objectMapper.readValue(
                messageBody,
                new TypeReference<Map<String, Object>>() {}
        );

        String hubId = (String) message.get("hubId");
        int stage = ((Number) message.get("stage")).intValue();

        store.updateStage(hubId, stage);
    }
}
package co.wethinkcode.logisticsconnect;

import java.util.HashMap;
import java.util.Map;

public class TransitDelayStageStore {

    private final Map<String, Integer> delayStages = new HashMap<>();

    public void updateStage(String hubId, int stage) {
        delayStages.put(hubId, stage);
    }

    public int getStage(String hubId) {
        return delayStages.getOrDefault(hubId, 0);
    }
}
package co.wethinkcode.logisticsconnect;

public interface DelayStagePublisher {
    void publish(String hubId, int stage);
}

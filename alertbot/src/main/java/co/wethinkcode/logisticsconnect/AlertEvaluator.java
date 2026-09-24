package co.wethinkcode.logisticsconnect;

public class AlertEvaluator {

    private final int threshold;

    public AlertEvaluator(int threshold) {
        this.threshold = threshold;
    }

    public boolean shouldAlert(int stage) {
        return stage >= threshold;
    }
}
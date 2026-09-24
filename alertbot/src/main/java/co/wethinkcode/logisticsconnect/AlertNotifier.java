package co.wethinkcode.logisticsconnect;

public interface AlertNotifier {

    void sendAlert(String hubId, int stage);
}
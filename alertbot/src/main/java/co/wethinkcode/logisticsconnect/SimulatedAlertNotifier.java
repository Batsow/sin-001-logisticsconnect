package co.wethinkcode.logisticsconnect;

public class SimulatedAlertNotifier implements AlertNotifier {

    @Override
    public void sendAlert(String hubId, int stage) {
        System.out.println(
                "ALERT: Hub " + hubId
                        + " has reached delay stage " + stage
        );
    }
}
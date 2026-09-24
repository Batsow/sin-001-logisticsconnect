package co.wethinkcode.logisticsconnect;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimulatedAlertNotifierTest {

    @Test
    void shouldPrintSimulatedAlert() {
        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(output));

            AlertNotifier notifier =
                    new SimulatedAlertNotifier();

            notifier.sendAlert("H-500", 7);

            String result = output.toString();

            assertTrue(result.contains("H-500"));
            assertTrue(result.contains("7"));
            assertTrue(result.contains("ALERT"));
        } finally {
            System.setOut(originalOut);
        }
    }
}
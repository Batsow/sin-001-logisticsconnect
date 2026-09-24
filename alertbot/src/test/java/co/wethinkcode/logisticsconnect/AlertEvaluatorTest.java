package co.wethinkcode.logisticsconnect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlertEvaluatorTest {

    @Test
    void shouldNotRaiseAlertBelowThreshold() {
        AlertEvaluator evaluator = new AlertEvaluator(5);

        assertFalse(evaluator.shouldAlert(4));
    }

    @Test
    void shouldRaiseAlertAtThreshold() {
        AlertEvaluator evaluator = new AlertEvaluator(5);

        assertTrue(evaluator.shouldAlert(5));
    }

    @Test
    void shouldRaiseAlertAboveThreshold() {
        AlertEvaluator evaluator = new AlertEvaluator(5);

        assertTrue(evaluator.shouldAlert(8));
    }
}
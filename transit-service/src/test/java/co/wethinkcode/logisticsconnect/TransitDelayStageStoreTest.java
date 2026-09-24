package co.wethinkcode.logisticsconnect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransitDelayStageStoreTest {

    @Test
    void shouldStoreDelayStageForHub() {
        TransitDelayStageStore store = new TransitDelayStageStore();

        store.updateStage("H-500", 5);

        assertEquals(5, store.getStage("H-500"));
    }
}
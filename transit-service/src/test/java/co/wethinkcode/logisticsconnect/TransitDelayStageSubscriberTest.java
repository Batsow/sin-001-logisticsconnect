package co.wethinkcode.logisticsconnect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransitDelayStageSubscriberTest {

    @Test
    void shouldUpdateStoreWhenDelayStageMessageIsReceived() throws Exception {
        TransitDelayStageStore store = new TransitDelayStageStore();

        TransitDelayStageSubscriber subscriber =
                new TransitDelayStageSubscriber(store);

        subscriber.handleMessage(
                "{\"hubId\":\"H-500\",\"stage\":5}"
        );

        assertEquals(5, store.getStage("H-500"));
    }
}
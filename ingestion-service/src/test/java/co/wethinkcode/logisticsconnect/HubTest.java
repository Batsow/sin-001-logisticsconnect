package co.wethinkcode.logisticsconnect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HubTest {
    @Test
    void shouldCreateHubWithCorrectValues() {
        Hub hub = new Hub(
                "H-500",
                "Gauteng",
                "Johannesburg Central",
                true
        );

        assertEquals("H-500", hub.getHubId());
        assertEquals("Gauteng", hub.getProvince());
        assertEquals("Johannesburg Central", hub.getSortingCenter());
        assertEquals(true, hub.getActive());
    }
}

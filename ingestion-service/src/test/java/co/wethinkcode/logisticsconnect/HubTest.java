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

    @Test
    void shouldReturnHubId() {
        Hub hub = new Hub(
                "H-501",
                "Western Cape",
                "Cape Town Port",
                true
        );

        assertEquals("H-501", hub.getHubId());
    }


    @Test
    void shouldReturnProvince() {
        Hub hub = new Hub(
                "H-502",
                "Gauteng",
                "Pretoria North",
                false
        );

        assertEquals("Gauteng", hub.getProvince());
    }


    @Test
    void shouldReturnSortingCenter() {
        Hub hub = new Hub(
                "H-503",
                "KwaZulu-Natal",
                "Durban Harbour",
                true
        );

        assertEquals("Durban Harbour", hub.getSortingCenter());
    }


    @Test
    void shouldReturnActiveStatus() {
        Hub hub = new Hub(
                "H-504",
                "Gauteng",
                "Johannesburg Central",
                true
        );

        assertEquals(true, hub.getActive());
    }


    @Test
    void shouldCreateHubWithCleanedValues() {
        HubDataCleaner cleaner = new HubDataCleaner();

        Hub hub = new Hub(
                cleaner.cleanHubId("h-501"),
                cleaner.cleanProvince("western cape"),
                cleaner.cleanSortingCenter("Cape Town  Port"),
                cleaner.cleanActive("yes")
        );

        assertEquals("H-501", hub.getHubId());
        assertEquals("Western Cape", hub.getProvince());
        assertEquals("Cape Town Port", hub.getSortingCenter());
        assertEquals(true, hub.getActive());
    }
}

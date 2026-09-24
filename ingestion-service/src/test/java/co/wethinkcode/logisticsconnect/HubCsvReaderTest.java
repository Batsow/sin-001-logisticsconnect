package co.wethinkcode.logisticsconnect;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HubCsvReaderTest {
    @Test
    void shouldReadOneHubFromCsv() {
        HubCsvReader reader = new HubCsvReader();

        List<Hub> hubs = reader.read("src/main/resources/hubs-global.csv");

        Hub firstHub = hubs.get(0);

        assertEquals("H-500", firstHub.getHubId());
        assertEquals("Gauteng", firstHub.getProvince());
        assertEquals("Johannesburg Central", firstHub.getSortingCenter());
        assertEquals(true, firstHub.getActive());
    }


    @Test
    void shouldReadAllHubsFromCsv() {
        HubCsvReader reader = new HubCsvReader();

        List<Hub> hubs = reader.read("src/main/resources/hubs-global.csv");

        assertEquals(12, hubs.size());
    }


    @Test
    void shouldRemoveDuplicateHubs() {
        HubCsvReader reader = new HubCsvReader();

        List<Hub> hubs = reader.read("src/main/resources/hubs-global.csv");

        long johannesburgCentralCount = hubs.stream()
                .filter(hub -> "Johannesburg Central".equals(hub.getSortingCenter()))
                .count();

        assertEquals(1, johannesburgCentralCount);
    }


    @Test
    void shouldKeepHubWithMissingProvince() {
        HubCsvReader reader = new HubCsvReader();

        List<Hub> hubs = reader.read("src/main/resources/hubs-global.csv");

        Hub hub = null;

        for (Hub currentHub : hubs) {
            if ("H-508".equals(currentHub.getHubId())) {
                hub = currentHub;
                break;
            }
        }

        assertEquals(null, hub.getProvince());
    }
}

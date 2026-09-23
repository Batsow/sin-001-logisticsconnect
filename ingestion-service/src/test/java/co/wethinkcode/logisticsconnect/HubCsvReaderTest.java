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
}

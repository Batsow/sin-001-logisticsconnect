package co.wethinkcode.logisticsconnect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HubDataCleanerTest {
    @Test
    void shouldTrimWhitespaceFromProvince() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanProvince(" Gauteng ");

        assertEquals("Gauteng", result);
    }

    @Test
    void shouldNormalizeProvinceCasing() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanProvince("gauteng");

        assertEquals("Gauteng", result);
    }

    @Test
    void shouldNormalizeMultiWordProvinceCasing() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanProvince("eastern cape");

        assertEquals("Eastern Cape", result);
    }


    @Test
    void shouldNormalizeKwaZuluNatal() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanProvince("Kwa-Zulu Natal");

        assertEquals("KwaZulu-Natal", result);
    }

    @Test
    void shouldCollapseMultipleSpacesInSortingCenter() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanSortingCenter("Cape Town  Port");

        assertEquals("Cape Town Port", result);
    }

    @Test
    void shouldNormalizeHubIdCasing() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanHubId("h-501");

        assertEquals("H-501", result);
    }

    @Test
    void shouldTrimWhitespaceFromHubId() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanHubId("H-502 ");

        assertEquals("H-502", result);
    }

    @Test
    void shouldNormalizeYesToTrue() {
        HubDataCleaner cleaner = new HubDataCleaner();

        Boolean result = cleaner.cleanActive("yes");

        assertEquals(true, result);
    }
}
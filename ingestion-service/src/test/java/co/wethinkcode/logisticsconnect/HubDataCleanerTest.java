package co.wethinkcode.logisticsconnect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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

    @Test
    void shouldNormalizeYToTrue() {
        HubDataCleaner cleaner = new HubDataCleaner();

        Boolean result = cleaner.cleanActive("Y");

        assertEquals(true, result);
    }


    @Test
    void shouldNormalizeOneToTrue() {
        HubDataCleaner cleaner = new HubDataCleaner();

        Boolean result = cleaner.cleanActive("1");

        assertEquals(true, result);
    }


    @Test
    void shouldNormalizeTrueToTrue() {
        HubDataCleaner cleaner = new HubDataCleaner();

        Boolean result = cleaner.cleanActive("true");

        assertEquals(true, result);
    }


    @Test
    void shouldNormalizeNToFalse() {
        HubDataCleaner cleaner = new HubDataCleaner();

        Boolean result = cleaner.cleanActive("N");

        assertEquals(false, result);
    }

    @Test
    void shouldNormalizeNoToFalse() {
        HubDataCleaner cleaner = new HubDataCleaner();

        Boolean result = cleaner.cleanActive("no");

        assertEquals(false, result);
    }

    @Test
    void shouldNormalizeZeroToFalse() {
        HubDataCleaner cleaner = new HubDataCleaner();

        Boolean result = cleaner.cleanActive("0");

        assertEquals(false, result);
    }


    @Test
    void shouldNormalizeFalseToFalse() {
        HubDataCleaner cleaner = new HubDataCleaner();

        Boolean result = cleaner.cleanActive("false");

        assertEquals(false, result);
    }

    @Test
    void shouldReturnNullForUnknownActiveValue() {
        HubDataCleaner cleaner = new HubDataCleaner();

        Boolean result = cleaner.cleanActive("unknown");

        assertNull(result);
    }

    @Test
    void shouldReturnNullForNotApplicableActiveValue() {
        HubDataCleaner cleaner = new HubDataCleaner();

        Boolean result = cleaner.cleanActive("N/A");

        assertNull(result);
    }


    @Test
    void shouldReturnNullForBlankProvince() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanProvince("");

        assertNull(result);
    }

    @Test
    void shouldReturnNullForBlankSpacesProvince() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanProvince("   ");

        assertNull(result);
    }


    @Test
    void shouldNormalizeKwaZuluNatalWithoutHyphen() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanProvince("KwaZulu Natal");

        assertEquals("KwaZulu-Natal", result);
    }


    @Test
    void shouldReturnNullForBlankSortingCenter() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanSortingCenter("   ");

        assertEquals(null, result);
    }


    @Test
    void shouldReturnNullForNullProvince() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanProvince(null);

        assertEquals(null, result);
    }


    @Test
    void shouldReturnNullForNullSortingCenter() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanSortingCenter(null);

        assertEquals(null, result);
    }

    @Test
    void shouldReturnNullForNullHubId() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanHubId(null);

        assertEquals(null, result);
    }


    @Test
    void shouldReturnNullForBlankHubId() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanHubId("   ");

        assertEquals(null, result);
    }


    @Test
    void shouldReturnNullForBlankActive() {
        HubDataCleaner cleaner = new HubDataCleaner();

        Boolean result = cleaner.cleanActive("   ");

        assertEquals(null, result);
    }


    @Test
    void shouldReturnNullForNullActive() {
        HubDataCleaner cleaner = new HubDataCleaner();

        Boolean result = cleaner.cleanActive(null);

        assertEquals(null, result);
    }


    @Test
    void shouldReturnNullForTbdActive() {
        HubDataCleaner cleaner = new HubDataCleaner();

        Boolean result = cleaner.cleanActive("TBD");

        assertEquals(null, result);
    }


    @Test
    void shouldReturnNullForDashActive() {
        HubDataCleaner cleaner = new HubDataCleaner();

        Boolean result = cleaner.cleanActive("-");

        assertEquals(null, result);
    }



    @Test
    void shouldReturnNullForNanActive() {
        HubDataCleaner cleaner = new HubDataCleaner();

        Boolean result = cleaner.cleanActive("NaN");

        assertEquals(null, result);
    }


    @Test
    void shouldReturnNullForUnknownProvince() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanProvince("unknown");

        assertEquals(null, result);
    }


    @Test
    void shouldReturnNullForNaProvince() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanProvince("N/A");

        assertEquals(null, result);
    }


    @Test
    void shouldReturnNullForTbdProvince() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanProvince("TBD");

        assertEquals(null, result);
    }


    @Test
    void shouldReturnNullForDashProvince() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanProvince("-");

        assertEquals(null, result);
    }


    @Test
    void shouldReturnNullForNanProvince() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanProvince("NaN");

        assertEquals(null, result);
    }


    @Test
    void shouldCollapseMultipleSpacesInProvince() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanProvince("Western  Cape");

        assertEquals("Western Cape", result);
    }


    @Test
    void shouldCleanSortingCenterCasing() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanSortingCenter("johannesburg central");

        assertEquals("Johannesburg Central", result);
    }

    @Test
    void shouldCleanSortingCenterMultipleSpaces() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanSortingCenter("Cape Town  Port");

        assertEquals("Cape Town Port", result);
    }

    @Test
    void shouldCleanHubIdCasing() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanHubId("h-501");

        assertEquals("H-501", result);
    }

    @Test
    void shouldCleanProvinceCasing() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanProvince("gauteng");

        assertEquals("Gauteng", result);
    }

    @Test
    void shouldHandleUnknownSortingCenter() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanSortingCenter("unknown");

        assertEquals(null, result);
    }


    @Test
    void shouldHandleNaSortingCenter() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanSortingCenter("N/A");

        assertEquals(null, result);
    }

    @Test
    void shouldHandleTbdSortingCenter() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanSortingCenter("TBD");

        assertEquals(null, result);
    }


    @Test
    void shouldHandleDashSortingCenter() {
        HubDataCleaner cleaner = new HubDataCleaner();

        String result = cleaner.cleanSortingCenter("-");

        assertEquals(null, result);
    }

}
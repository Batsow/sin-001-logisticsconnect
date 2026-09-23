package co.wethinkcode.logisticsconnect;

public class HubDataCleaner {

    public String cleanProvince(String province) {
        province = province.trim().toLowerCase();

        if (province.isEmpty()) {
            return null;
        }

        String[] words = province.split(" ");
        String result = "";

        for (String word : words) {
            String firstLetter = word.substring(0, 1).toUpperCase();
            String remainingLetters = word.substring(1);

            result = result + firstLetter + remainingLetters + " ";
        }
        result = result.trim();

        if (result.equals("Kwa-zulu Natal") || result.equals("Kwazulu Natal")) {
            result = "KwaZulu-Natal";
        }

        return result;
    }


    public String cleanSortingCenter(String sortingCenter) {
        sortingCenter = sortingCenter.trim();

        if (sortingCenter.isEmpty()) {
            return null;
        }

        String[] words = sortingCenter.split(" ");
        String result = "";

        for (String word : words) {
            if (!word.isEmpty()) {
                result = result + word + " ";
            }
        }

        return result.trim();
    }
    

    public String cleanHubId(String hubId) {
        hubId = hubId.trim();

        return hubId.toUpperCase();
    }


    public Boolean cleanActive(String active) {
        active = active.trim().toLowerCase();

        if (active.equals("yes") || active.equals("y") || active.equals("1") || active.equals("true")) {
            return true;
        }

        if (active.equals("n") || active.equals("no") || active.equals("0") || active.equals("false")) {
            return false;
        }

        return null;
    }


    public static class HubCsvReaderTest {
    }
}
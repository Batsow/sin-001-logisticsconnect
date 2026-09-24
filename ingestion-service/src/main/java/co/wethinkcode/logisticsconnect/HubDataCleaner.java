package co.wethinkcode.logisticsconnect;

public class HubDataCleaner {

    public String cleanProvince(String province) {
        if (province == null) {
            return null;
        }

        province = province.trim().toLowerCase();

        if (province.isEmpty()) {
            return null;
        }

        if (province.equals("unknown")) {
            return null;
        }

        if (province.equals("n/a")) {
            return null;
        }

        if (province.equals("tbd")) {
            return null;
        }

        if (province.equals("-")) {
            return null;
        }

        if (province.equals("nan")) {
            return null;
        }

        String[] words = province.split(" ");
        String result = "";

        for (String word : words) {
            if (!word.isEmpty()) {
                String firstLetter = word.substring(0, 1).toUpperCase();
                String remainingLetters = word.substring(1);

                result = result + firstLetter + remainingLetters + " ";
            }
        }

        result = result.trim();

        if (result.equals("Kwa-zulu Natal") || result.equals("Kwazulu Natal")) {
            result = "KwaZulu-Natal";
        }

        return result;
    }


    public String cleanSortingCenter(String sortingCenter) {
        if (sortingCenter == null) {
            return null;
        }

        sortingCenter = sortingCenter.trim();

        if (sortingCenter.isEmpty()) {
            return null;
        }

        if (sortingCenter.equalsIgnoreCase("unknown")) {
            return null;
        }

        if (sortingCenter.equalsIgnoreCase("n/a")) {
            return null;
        }

        String[] words = sortingCenter.split(" ");
        String result = "";

        for (String word : words) {
            if (!word.isEmpty()) {
                String firstLetter = word.substring(0, 1).toUpperCase();
                String remainingLetters = word.substring(1).toLowerCase();

                result = result + firstLetter + remainingLetters + " ";
            }
        }

        return result.trim();
    }
    

    public String cleanHubId(String hubId) {
        if (hubId == null) {
            return null;
        }

        hubId = hubId.trim();

        if (hubId.isEmpty()) {
            return null;
        }

        return hubId.toUpperCase();
    }


    public Boolean cleanActive(String active) {
        if (active == null) {
            return null;
        }

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
package co.wethinkcode.logisticsconnect;

public class HubDataCleaner {

    public String cleanProvince(String province) {
        province = province.trim().toLowerCase();

        String[] words = province.split(" ");
        String result = "";

        for (String word : words) {
            String firstLetter = word.substring(0, 1).toUpperCase();
            String remainingLetters = word.substring(1);

            result = result + firstLetter + remainingLetters + " ";
        }
        result = result.trim();

        if (result.equals("Kwa-zulu Natal")) {
            result = "KwaZulu-Natal";
        }

        return result;
    }


    public String cleanSortingCenter(String sortingCenter) {
        sortingCenter = sortingCenter.trim();

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

        if (active.equals("yes")) {
            return true;
        }

        return null;
    }


}
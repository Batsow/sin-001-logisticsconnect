package co.wethinkcode.logisticsconnect;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HubCsvReader {
    public List<Hub> read(String filePath) {
        List<Hub> hubs = new ArrayList<>();

        try {
            CSVReader reader = new CSVReader(new FileReader(filePath));

            reader.readNext();

            HubDataCleaner cleaner = new HubDataCleaner();

            Set<String> seenHubs = new HashSet<>();

            String[] row;

            while ((row = reader.readNext()) != null) {

                Hub hub = new Hub(
                        cleaner.cleanHubId(row[0]),
                        cleaner.cleanProvince(row[1]),
                        cleaner.cleanSortingCenter(row[2]),
                        cleaner.cleanActive(row[3])
                );

                String duplicateKey = hub.getProvince() + "|" + hub.getSortingCenter();

                if (!seenHubs.contains(duplicateKey)) {
                    hubs.add(hub);
                    seenHubs.add(duplicateKey);
                }
            }
            reader.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return hubs;
    }
}

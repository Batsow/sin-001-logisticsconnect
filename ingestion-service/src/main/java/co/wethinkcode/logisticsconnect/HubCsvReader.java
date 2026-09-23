package co.wethinkcode.logisticsconnect;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class HubCsvReader {
    public List<Hub> read(String filePath) {
        List<Hub> hubs = new ArrayList<>();

        try {
            CSVReader reader = new CSVReader(new FileReader(filePath));

            reader.readNext();

            HubDataCleaner cleaner = new HubDataCleaner();

            String[] row;

            while ((row = reader.readNext()) != null) {

                Hub hub = new Hub(
                        cleaner.cleanHubId(row[0]),
                        cleaner.cleanProvince(row[1]),
                        cleaner.cleanSortingCenter(row[2]),
                        cleaner.cleanActive(row[3])
                );

                hubs.add(hub);
            }

            reader.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return hubs;
    }
}

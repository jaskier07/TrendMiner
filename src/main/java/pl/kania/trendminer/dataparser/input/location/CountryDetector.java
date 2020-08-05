package pl.kania.trendminer.dataparser.input.location;

import lombok.Value;
import lombok.extern.jbosslog.JBossLog;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.kania.trendminer.ParserExecutionException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;


/**
 * https://datahub.io/core/world-cities
 */
@JBossLog
public class CountryDetector {

    private static final Map<String, String> COUNTRY_PER_NAME = new HashMap<>();
    private static final Map<String, String> COUNTRY_PER_SUBCOUNTRY = new HashMap<>();
    private static final Set<String> COUNTRIES = new HashSet<>();
    private final String pathToFile;

    public CountryDetector(String pathToFile) {
        this.pathToFile = pathToFile;
    }

    public Optional<String> detect(String place) {
        if (Strings.isBlank(place)) {
            return Optional.empty();
        }
        if (COUNTRY_PER_NAME.isEmpty()) {
            initPlaces();
        }

        place = place.toLowerCase();
        String[] places = place.split(",");
        for (String p : places) {
            if (COUNTRY_PER_NAME.containsKey(p)) {
                return Optional.of(COUNTRY_PER_NAME.get(p));
            } else if (COUNTRY_PER_SUBCOUNTRY.containsKey(p)) {
                return Optional.of(COUNTRY_PER_SUBCOUNTRY.get(p));
            } else if (COUNTRIES.contains(p)) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    private void initPlaces() {
        try (InputStream is = getClass().getResourceAsStream(pathToFile);
             InputStreamReader input = new InputStreamReader(is)
        ) {
            CSVParser csvParser = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(input);
            for (CSVRecord record : csvParser) {
                try {
                    Place place = getPlace(record);
                    COUNTRY_PER_SUBCOUNTRY.put(place.getSubcountry(), place.getCountry());
                    COUNTRY_PER_NAME.put(place.getName(), place.getCountry());
                    COUNTRIES.add(place.getCountry());
                } catch (Exception e) {
                    log.warn("Problem with reading record. Record number: " + record.getRecordNumber(), e);
                }
            }
        } catch (IOException e) {
            log.error("Cannot find csv containing places", e);
            throw new ParserExecutionException(e.getMessage());
        }
    }

    private static Place getPlace(CSVRecord record) {
        String name = record.get("name").toLowerCase();
        String country = record.get("country").toLowerCase();
        String subcountry = record.get("subcountry").toLowerCase();
        return new Place(name, country, subcountry);
    }
}

@Value
class Place {
    String name;
    String country;
    String subcountry;
}
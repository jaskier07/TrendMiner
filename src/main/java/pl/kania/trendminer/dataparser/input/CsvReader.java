package pl.kania.trendminer.dataparser.input;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.util.Strings;
import pl.kania.trendminer.dataparser.Tweet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CsvReader {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEE LLL dd HH:mm:ss xxxx yyyy");

    public List<Tweet> readFile() {
        List<Tweet> tweets = new ArrayList<>();

        File file = new File(Paths.get(System.getProperty("user.dir") + "/src/main/resources/corona-0-15000.csv").toString());

        try (InputStream is = new FileInputStream(file);
             InputStreamReader input = new InputStreamReader(is)) {

            CSVParser csvParser = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(input);
            for (CSVRecord record : csvParser) {
                try {
                    Tweet tweet = getTweetFromRecord(record);
                    tweets.add(tweet);
                } catch (IllegalArgumentException e) {
                    log.warn("Problem with reading record. Record number: " + record.getRecordNumber(), e);
                }
            }
        } catch (IOException e) {
            log.error("Cannot find csv containing tweets", e);
        }
        return tweets;
    }

    private Tweet getTweetFromRecord(CSVRecord record) {
        Tweet tweet = new Tweet();
        tweet.setCreatedAt(parseDate(record.get("created_at")));
        tweet.setId(record.get("id"));
        tweet.setAuthor(record.get("user_screen_name"));
        tweet.setContent(parseContent(record.get("text")));
        tweet.setLang(record.get("lang"));
        tweet.setLocation(record.get("user_location"));
        return tweet;
    }

    private String parseContent(String text) {
        text = text.replaceFirst("^RT @(.*):", "");
        text = text.replaceFirst("^@[^\\s]*", "");
        return text;
    }

    private LocalDateTime parseDate(String value) {
        if (Strings.isBlank(value)) {
            return null;
        }
        try {
            return LocalDateTime.from(dtf.parse(value));
        } catch (DateTimeParseException e) {
            log.error("Cannot parse date: " + value, e);
            return null;
        }
    }
}

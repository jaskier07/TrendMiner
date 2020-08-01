package pl.kania.trendminer.dataparser.input;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.util.Strings;
import pl.kania.trendminer.ParserExecutionException;
import pl.kania.trendminer.util.ProgressLogger;
import pl.kania.trendminer.dataparser.Tweet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CsvReader {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEE LLL dd HH:mm:ss xxxx yyyy");

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public TweetAnalysisData readFile(String path) {
        startTime = LocalDateTime.MAX;
        endTime = LocalDateTime.MIN;
        List<Tweet> tweets = new ArrayList<>();

        try (InputStream is = getClass().getResourceAsStream(path);
             InputStreamReader input = new InputStreamReader(is)
        ) {
            log.info("File is being read...");
            CSVParser csvParser = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(input);
            for (CSVRecord record : csvParser) {
                try {
                    Tweet tweet = getTweetFromRecord(record);
                    tweets.add(tweet);

                    ProgressLogger.log(record.getRecordNumber());
                } catch (Exception e) {
                    log.warn("Problem with reading record. Record number: " + record.getRecordNumber(), e);
                }
            }
            ProgressLogger.done();

            log.info("Finished reading file.");
            return new TweetAnalysisData(tweets, startTime, endTime);
        } catch (IOException e) {
            log.error("Cannot find csv containing tweets", e);
            throw new ParserExecutionException(e.getMessage());
        }
    }

    private Tweet getTweetFromRecord(CSVRecord record) {
        Tweet tweet = new Tweet();
        tweet.setCreatedAt(parseDate(record.get("created_at")));
        tweet.setId(record.get("id"));
        tweet.setAuthor(record.get("user_screen_name"));
        tweet.setContent(record.get("text"));
        tweet.setLang(record.get("lang"));
        tweet.setLocation(record.get("user_location"));
        return tweet;
    }

    private LocalDateTime parseDate(String value) {
        if (Strings.isBlank(value)) {
            return null;
        }
        try {
            LocalDateTime date = LocalDateTime.from(dtf.parse(value));
            if (date.isBefore(startTime)) {
                startTime = date;
            }
            if (date.isAfter(endTime)) {
                endTime = date;
            }
            return date;
        } catch (DateTimeParseException e) {
            log.error("Cannot parse date: " + value, e);
            return null;
        }
    }
}

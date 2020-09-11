package pl.kania.trendminer.dataparser.preproc.filtering;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.kania.trendminer.ParserExecutionException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service("wordnet")
public class EnglishDictionaryWordNet implements Dictionary {

    private static final int LINES_TO_SKIP = 29;
    public static final int WORD_COLUMN_INDEX_IN_LINE = 0;
    private static final List<String> STOPWORDS = Arrays.asList("coronavirus", "could", "covid-19", "covid");
    private final Set<String> dictionary;

    public EnglishDictionaryWordNet(@Autowired Environment environment) {
        dictionary = new HashSet<>();

        loadWordsFromFile(environment.getProperty("pl.kania.path.wordnet-adj"));
        loadWordsFromFile(environment.getProperty("pl.kania.path.wordnet-adv"));
        loadWordsFromFile(environment.getProperty("pl.kania.path.wordnet-noun"));
        loadWordsFromFile(environment.getProperty("pl.kania.path.wordnet-verb"));
        dictionary.addAll(STOPWORDS);
    }

    public boolean isEnglishWord(String word) {
        return dictionary.contains(word);
    }

    public int getSize() {
        return dictionary.size();
    }

    private void loadWordsFromFile(String path) {
        try (InputStream is = getClass().getResourceAsStream(path);
             InputStreamReader input = new InputStreamReader(is)
        ) {
            int lineIndex = 0;
            CSVParser csvParser = CSVFormat.EXCEL.withDelimiter(' ').parse(input);
            for (CSVRecord record : csvParser) {
                try {
                    if (lineIndex++ > LINES_TO_SKIP) {
                        String word = record.get(WORD_COLUMN_INDEX_IN_LINE);
                        dictionary.addAll(Arrays.asList(word.split("_")));
                    }
                } catch (Exception e) {
                    log.warn("Problem with reading record. Record number: " + record.getRecordNumber(), e);
                }
            }
        } catch (IOException e) {
            log.error("Cannot find file " + path, e);
            throw new ParserExecutionException(e.getMessage());
        }
    }
}

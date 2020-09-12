package pl.kania.trendminer.db.dao;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.kania.trendminer.DataParserApplication;
import pl.kania.trendminer.dataparser.parser.AnalysedPeriod;
import pl.kania.trendminer.dataparser.parser.WordCooccurrence;
import pl.kania.trendminer.db.model.Word;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Disabled("Saves data in database - for debugging only")
@SpringBootTest(classes = DataParserApplication.class)
class DatabaseServiceTest {

    @Autowired
    private DatabaseService databaseService;

    @Test
    public void given35WordsCheckIfArePersistedIn2Batches() throws Exception {
        log.info("-------------------- NO BATCHING ---------------------");

        AnalysedPeriod period = getFilledAnalysedPeriod(0);
        databaseService.saveAllPeriods(Collections.singletonList(period));

        log.info("-------------------- BATCHING ---------------------");

        period = getFilledAnalysedPeriod(1);
        databaseService.saveAllPeriods(Collections.singletonList(period));
    }

    private AnalysedPeriod getFilledAnalysedPeriod(int start) {
        List<Word> words = getWords();
        AnalysedPeriod period = new AnalysedPeriod(LocalDateTime.now().plusDays(start), LocalDateTime.now().plusDays(start + 1));
        for (int i = 1; i < words.size(); i++) {
            WordCooccurrence wc = new WordCooccurrence(words.get(i - 1).getWord(), words.get(i).getWord());
            period.getCooccurrenceCountPerDocument().put(wc, 1L);
        }
        return period;
    }

    private List<Word> getWords() {
        return IntStream.range(0, 31)
                .mapToObj(i -> new Word("test" + i))
                .collect(Collectors.toList());
    }
}
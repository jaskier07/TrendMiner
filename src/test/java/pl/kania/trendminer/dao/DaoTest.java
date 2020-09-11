package pl.kania.trendminer.dao;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import pl.kania.trendminer.DataParserApplication;
import pl.kania.trendminer.model.Word;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(classes = DataParserApplication.class)
class DaoTest {

    @Autowired
    private WordDao wordDao;

    @Test
    public void given35WordsCheckIfArePersistedIn2Batches() throws Exception {
        log.info("-------------------- NO BATCHING ---------------------");

        List<Word> words = getWords();
        words.forEach(wordDao::save);

        log.info("-------------------- BATCHING ---------------------");

        words = getWords();
        wordDao.saveAll(words);
    }

    private List<Word> getWords() {
        return IntStream.range(0, 35)
                .mapToObj(i -> new Word("test" + i))
                .collect(Collectors.toList());
    }
}
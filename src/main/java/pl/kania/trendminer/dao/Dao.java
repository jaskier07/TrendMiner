package pl.kania.trendminer.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kania.trendminer.dataparser.parser.AnalysedPeriod;
import pl.kania.trendminer.dataparser.parser.WordCooccurrence;
import pl.kania.trendminer.model.Cooccurrence;
import pl.kania.trendminer.model.TimeId;
import pl.kania.trendminer.model.Word;
import pl.kania.trendminer.util.Counter;

import javax.persistence.EntityManager;
import java.util.*;

@Slf4j
@Service
public class Dao {

    private static final int BATCH_SIZE = 30;
    private final CooccurrenceDao cooccurrenceDao;
    private final TimeIdDao timeIDDao;
    private final WordDao wordDao;
    private final EntityManager em;

    public Dao(@Autowired CooccurrenceDao cooccurrenceDao, @Autowired TimeIdDao timeIDDao, @Autowired WordDao wordDao, @Autowired EntityManager em) {
        this.cooccurrenceDao = cooccurrenceDao;
        this.timeIDDao = timeIDDao;
        this.wordDao = wordDao;
        this.em = em;
    }

    public void saveAllPeriods(List<AnalysedPeriod> periods) {
        Counter periodIndex = new Counter();
        Map<String, Word> savedWords = new HashMap<>();
        Map<String, Word> wordsToSaveInBatch = new HashMap<>();
        Set<Cooccurrence> savedCooccurrences = new HashSet<>();
        Set<Cooccurrence> cooccurrencesToSaveInBatch = new HashSet<>();

        periods.forEach(period -> {
            TimeId timeID = getNewTimeId(period, periodIndex.getValueAndIncrement());
            timeID = timeIDDao.save(timeID);
            log.info("Saved time period: " + timeID);

            Map<WordCooccurrence, Long> cooccurrenceCountPerDocument = period.getCooccurrenceCountPerDocument();
            for (Map.Entry<WordCooccurrence, Long> entry : cooccurrenceCountPerDocument.entrySet()) {
                WordCooccurrence cooccurrence = entry.getKey();

                if (!cooccurrence.getWord1().equals(cooccurrence.getWord2())) {
                    Word word1 = getWord(cooccurrence.getWord1(), savedWords, wordsToSaveInBatch);
                    Word word2 = getWord(cooccurrence.getWord2(), savedWords, wordsToSaveInBatch);
                    Cooccurrence coocToSave = getCooccurrence(timeID, word1, word2, cooccurrence.getSupport());
                    if (!savedCooccurrences.contains(coocToSave)) {
                        cooccurrencesToSaveInBatch.add(coocToSave);
                    }

                    if (wordsToSaveInBatch.size() >= BATCH_SIZE) {
                        saveWords(wordsToSaveInBatch, savedWords);
                    }
                    if (cooccurrencesToSaveInBatch.size() > BATCH_SIZE) {
                        saveWords(wordsToSaveInBatch, savedWords);
                        cooccurrenceDao.saveAll(cooccurrencesToSaveInBatch);
                        cooccurrenceDao.flush();
                        savedCooccurrences.addAll(cooccurrencesToSaveInBatch);
                        cooccurrencesToSaveInBatch.clear();
                    }
                }
            }
        });

        if (!wordsToSaveInBatch.isEmpty()) {
            wordDao.saveAll(wordsToSaveInBatch.values());
        }
        if (!cooccurrencesToSaveInBatch.isEmpty()) {
            cooccurrenceDao.saveAll(cooccurrencesToSaveInBatch);
        }
    }

    private void saveWords(Map<String, Word> wordsToSaveInBatch, Map<String, Word> savedWords) {
        wordDao.saveAll(wordsToSaveInBatch.values());
        wordDao.flush();
        savedWords.putAll(wordsToSaveInBatch);
        wordsToSaveInBatch.clear();
    }

    private Word getWord(String word, Map<String, Word> savedWords, Map<String, Word> wordsToSaveInBatch) {
        if (savedWords.containsKey(word)) {
            return savedWords.get(word);
        } else if (wordsToSaveInBatch.containsKey(word)) {
            return wordsToSaveInBatch.get(word);
        }

        Word newWord = new Word(word);
        wordsToSaveInBatch.put(word, newWord);
        return newWord;
    }

    private TimeId getNewTimeId(AnalysedPeriod period, int periodIndex) {
        TimeId timeID = new TimeId();
        timeID.setDocFreq(period.getAllDocumentsCount());
        timeID.setStartTime(period.getStart());
        timeID.setEndTime(period.getEnd());
        timeID.setIndex(periodIndex);
        return timeID;
    }

    private Cooccurrence getCooccurrence(TimeId timeID, Word word1, Word word2, Double support) {
        return new Cooccurrence(word1, word2, timeID, support);
    }

    @Transactional
    public void deleteAll() {
        em.createNativeQuery("DROP TABLE IF EXISTS `cooccurrence`")
                .executeUpdate();

        em.createNativeQuery("DROP TABLE IF EXISTS `time_id`")
                .executeUpdate();

        em.createNativeQuery("DROP TABLE IF EXISTS `word`")
                .executeUpdate();

        em.createNativeQuery("CREATE TABLE `word` (\n" +
                "  `id` bigint NOT NULL,\n" +
                "  `word` varchar(255) DEFAULT NULL,\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;")
                .executeUpdate();

        em.createNativeQuery("CREATE TABLE `time_id` (\n" +
                "  `id` bigint NOT NULL,\n" +
                "  `doc_freq` bigint DEFAULT NULL,\n" +
                "  `end_time` datetime DEFAULT NULL,\n" +
                "  `start_time` datetime DEFAULT NULL,\n" +
                "  `period_index` bigint DEFAULT NULL,\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;")
                .executeUpdate();

        em.createNativeQuery("CREATE TABLE `cooccurrence` (\n" +
                "  `id` bigint NOT NULL,\n" +
                "  `support` double DEFAULT NULL,\n" +
                "  `time_id` bigint DEFAULT NULL,\n" +
                "  `word_1_id` bigint DEFAULT NULL,\n" +
                "  `word_2_id` bigint DEFAULT NULL,\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  KEY `FK5isl3n1byuixtv7n0p4raxds7` (`time_id`),\n" +
                "  KEY `FKggybbwugsdvp18fi55swa1273` (`word_1_id`),\n" +
                "  KEY `FKb3k0u3boqkbkg8iock6yvm4gr` (`word_2_id`)\n" +
                ") ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;")
        .executeUpdate();
    }
}

package pl.kania.trendminer.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kania.trendminer.dataparser.parser.AnalysedPeriod;
import pl.kania.trendminer.dataparser.parser.WordCooccurrence;
import pl.kania.trendminer.model.Cooccurrence;
import pl.kania.trendminer.model.TimeId;
import pl.kania.trendminer.model.Word;

import java.util.*;

@Slf4j
@Service
public class Dao {

    private static final int BATCH_SIZE = 30;
    private final CooccurrenceDao cooccurrenceDao;
    private final TimeIdDao timeIDDao;
    private final WordDao wordDao;

    public Dao(@Autowired CooccurrenceDao cooccurrenceDao, @Autowired TimeIdDao timeIDDao, @Autowired WordDao wordDao) {
        this.cooccurrenceDao = cooccurrenceDao;
        this.timeIDDao = timeIDDao;
        this.wordDao = wordDao;
    }

    public void saveAllPeriods(List<AnalysedPeriod> periods) {
        Map<String, Word> savedWords = new HashMap<>();
        Map<String, Word> wordsToSaveInBatch = new HashMap<>();
        Set<Cooccurrence> cooccurrencesToSaveInBatch = new HashSet<>();

        periods.forEach(period -> {
            TimeId timeID = getNewTimeId(period);
            timeID = timeIDDao.save(timeID);
            log.info("Saved time period: " + timeID);

            Map<WordCooccurrence, Long> cooccurrenceCountPerDocument = period.getCooccurrenceCountPerDocument();
            for (Map.Entry<WordCooccurrence, Long> entry : cooccurrenceCountPerDocument.entrySet()) {
                WordCooccurrence cooccurrence = entry.getKey();

                if (!cooccurrence.getWord1().equals(cooccurrence.getWord2())) {
                    Word word1 = getWord(cooccurrence.getWord1(), savedWords, wordsToSaveInBatch);
                    Word word2 = getWord(cooccurrence.getWord2(), savedWords, wordsToSaveInBatch);
                    Cooccurrence coocToSave = getCooccurrence(timeID, word1, word2, cooccurrence.getSupport());
                    cooccurrencesToSaveInBatch.add(coocToSave);

                    if (wordsToSaveInBatch.size() >= BATCH_SIZE) {
                        saveWords(wordsToSaveInBatch);
                    }
                    if (cooccurrencesToSaveInBatch.size() > BATCH_SIZE) {
                        saveWords(wordsToSaveInBatch);
                        cooccurrenceDao.saveAll(cooccurrencesToSaveInBatch);
                        cooccurrenceDao.flush();
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

    private void saveWords(Map<String, Word> wordsToSaveInBatch) {
        wordDao.saveAll(wordsToSaveInBatch.values());
        wordDao.flush();
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

    private TimeId getNewTimeId(AnalysedPeriod period) {
        TimeId timeID = new TimeId();
        timeID.setDocFreq(period.getAllDocumentsCount());
        timeID.setStartTime(period.getStart());
        timeID.setEndTime(period.getEnd());
        return timeID;
    }

    private void saveCooccurrence(TimeId timeID, Word word1, Word word2, Double support) {
        Cooccurrence cooccurrence = getCooccurrence(timeID, word1, word2, support);
        cooccurrenceDao.save(cooccurrence);
        log.debug("Saved word cooccurrence: " + cooccurrence);
    }

    private Cooccurrence getCooccurrence(TimeId timeID, Word word1, Word word2, Double support) {
        return new Cooccurrence(word1, word2, timeID, support);
    }

    private Word getWord(String word) {
        return wordDao.findFirstByWordEquals(word).orElseGet(() -> wordDao.save(new Word(word)));
    }

    public void deleteAll() {
        log.info("Removing existing records...");
        cooccurrenceDao.deleteAll();
        timeIDDao.deleteAll();
        wordDao.deleteAll();
        log.info("Done.");
    }
}

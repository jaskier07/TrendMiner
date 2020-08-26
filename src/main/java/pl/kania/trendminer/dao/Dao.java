package pl.kania.trendminer.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kania.trendminer.dataparser.parser.AnalysedPeriod;
import pl.kania.trendminer.dataparser.parser.WordCooccurrence;
import pl.kania.trendminer.model.Cooccurrence;
import pl.kania.trendminer.model.TimeId;
import pl.kania.trendminer.model.Word;

import java.util.Map;

@Slf4j
@Service
public class Dao {

    private CooccurrenceDao cooccurrenceDao;
    private TimeIdDao timeIDDao;
    private WordDao wordDao;

    public Dao(@Autowired CooccurrenceDao cooccurrenceDao, @Autowired TimeIdDao timeIDDao, @Autowired WordDao wordDao) {
        this.cooccurrenceDao = cooccurrenceDao;
        this.timeIDDao = timeIDDao;
        this.wordDao = wordDao;
    }

    public void saveTimePeriod(AnalysedPeriod period) {
        Map<WordCooccurrence, Long> cooccurrenceCountPerDocument = period.getCooccurrenceCountPerDocument();

        TimeId timeID = new TimeId();
        timeID.setDocFreq(period.getAllDocumentsCount());
        timeID.setStartTime(period.getStart());
        timeID.setEndTime(period.getEnd());

        timeID = timeIDDao.save(timeID);
        log.info("Saved time period: " + timeID);

        for (Map.Entry<WordCooccurrence, Long> entry : cooccurrenceCountPerDocument.entrySet()) {
            WordCooccurrence cooccurrenceEntry = entry.getKey();
            Word word1 = getWord(cooccurrenceEntry.getWord1());
            Word word2 = getWord(cooccurrenceEntry.getWord2());
            if (!word1.getWord().equals(word2.getWord())) {
                saveCooccurrence(timeID, word1, word2, cooccurrenceEntry.getSupport());
            } else {
                log.debug("Words in cooccurrence are the same! " + word1);
            }
        }

        log.info("Done saving word cooccurrences. Saved " + cooccurrenceCountPerDocument.size() + " records.");
    }

    private void saveCooccurrence(TimeId timeID, Word word1, Word word2, Double support) {
        Cooccurrence cooccurrence = new Cooccurrence(word1, word2, timeID, support);
        cooccurrenceDao.save(cooccurrence);
        log.debug("Saved word cooccurrence: " + cooccurrence);
    }

    private Word getWord(String word) {
        return wordDao.findFirstByWordEquals(word).orElseGet(() -> wordDao.save(new Word(word)));
    }
}

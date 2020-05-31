package pl.kania.trendminer.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kania.trendminer.model.Cooccurrence;
import pl.kania.trendminer.model.TimeID;
import pl.kania.trendminer.model.Word;
import pl.kania.trendminer.parser.WordCooccurrence;

import java.util.Map;

@Service
public class Dao {

    private CooccurrenceDao cooccurrenceDao;
    private TimeIDDao timeIDDao;
    private WordDao wordDao;

    public Dao(@Autowired CooccurrenceDao cooccurrenceDao, @Autowired TimeIDDao timeIDDao, @Autowired WordDao wordDao) {
        this.cooccurrenceDao = cooccurrenceDao;
        this.timeIDDao = timeIDDao;
        this.wordDao = wordDao;
    }

    public void saveTimePeriod(Map<WordCooccurrence, Long> cooccurrenceCountPerDocument, long allDocumentsCount) {
        TimeID timeID = new TimeID();
        timeID.setDocFreq(allDocumentsCount);
        // TODO set periods

        timeID = timeIDDao.save(timeID);
        for (Map.Entry<WordCooccurrence, Long> entry : cooccurrenceCountPerDocument.entrySet()) {
            WordCooccurrence cooccurrenceEntry = entry.getKey();
            Word word1 = getWord(cooccurrenceEntry.getWord1());
            Word word2 = getWord(cooccurrenceEntry.getWord2());
            saveCooccurrence(timeID, word1, word2, cooccurrenceEntry.getSupport());
        }
    }

    private void saveCooccurrence(TimeID timeID, Word word1, Word word2, Double support) {
        Cooccurrence cooccurrence = new Cooccurrence(word1, word2, timeID, support);
        cooccurrenceDao.save(cooccurrence);
    }

    private Word getWord(String word) {
        return wordDao.findFirstByWordEquals(word).orElseGet(() -> wordDao.save(new Word(word)));
    }
}

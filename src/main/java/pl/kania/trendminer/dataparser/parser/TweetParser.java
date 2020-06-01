package pl.kania.trendminer.dataparser.parser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.kania.trendminer.dao.Dao;
import pl.kania.trendminer.dataparser.Tweet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class TweetParser {

    private static final double SUPPORT_MIN_THRESHOLD = 0.05; // TODO
    private OpenNlpProvider openNlpProvider;
    private Dao dao;

    public TweetParser(@Autowired Dao dao) {
        this.openNlpProvider = new OpenNlpProvider();
        this.dao = dao;
    }

    public void parseWordsFromTweetsAndFillCooccurrenceTable(List<Tweet> tweetsInEnglish) {
        Map<WordCooccurrence, Long> cooccurrenceCount = new HashMap<>();
        Map<WordCooccurrence, Long> cooccurrenceCountPerDocument = new HashMap<>();

        fillCooccurrenceTables(tweetsInEnglish, cooccurrenceCount, cooccurrenceCountPerDocument);
        setSupportValuesAndDropUncommonCooccurrences(tweetsInEnglish, cooccurrenceCountPerDocument);

        dao.saveTimePeriod(cooccurrenceCountPerDocument, tweetsInEnglish.size());
    }

    private void fillCooccurrenceTables(List<Tweet> tweetsInEnglish, Map<WordCooccurrence, Long> cooccurrenceCount, Map<WordCooccurrence, Long> cooccurrenceCountPerDocument) {
        for (Tweet tweet : tweetsInEnglish) {
            String[] sentences = openNlpProvider.divideIntoSentences(tweet.getContent());
            List<String> stemmedWords = getStemmedWords(sentences);

            if (stemmedWords.size() > 1) {
                tweet.setStemmedWords(Set.copyOf(stemmedWords));
                // FIXME shouldn't it be per sentence, not whole tweet content?
                addWordsToCooccurrenceMap(stemmedWords, cooccurrenceCount, cooccurrenceCountPerDocument);
            }
        }
        log.info("Done filling cooccurrence tables. Found word cooccurrences: " + cooccurrenceCountPerDocument.size());
    }

    private void setSupportValuesAndDropUncommonCooccurrences(List<Tweet> tweetsInEnglish, Map<WordCooccurrence, Long> cooccurrenceCountPerDocument) {
        Iterator<Map.Entry<WordCooccurrence, Long>> iterator = cooccurrenceCountPerDocument.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<WordCooccurrence, Long> entry = iterator.next();
            WordCooccurrence wordCooccurrence = entry.getKey();
            double support = (double)entry.getValue() / tweetsInEnglish.size();
            if (support < SUPPORT_MIN_THRESHOLD) {
                iterator.remove();
                log.debug("Dropped word cooccurrence: " + wordCooccurrence.toString() + " with support = " + support);
            } else {
                wordCooccurrence.setSupport(support);
                log.debug("Preserved word cooccurrence " + wordCooccurrence.toString() + " with support = " + support);
            }
        }
        log.info("Done setting support values. Preserved word cooccurrences: " + cooccurrenceCountPerDocument.size());
    }

    private void addWordsToCooccurrenceMap(List<String> stemmedWords, Map<WordCooccurrence, Long> cooccurrenceCount, Map<WordCooccurrence, Long> cooccurrenceCountPerDocument) {
        Set<WordCooccurrence> updatedWordCooccurrences = new HashSet<>();

        for (int i = 0; i < stemmedWords.size(); i++) {
            for (int j = i + 1; j < stemmedWords.size(); j++) {
                WordCooccurrence wordCooccurrence = new WordCooccurrence(stemmedWords.get(i), stemmedWords.get(j));
                cooccurrenceCount.merge(wordCooccurrence, 1L, Long::sum);
                if (!updatedWordCooccurrences.contains(wordCooccurrence)) {
                    cooccurrenceCountPerDocument.merge(wordCooccurrence, 1L, Long::sum);
                    updatedWordCooccurrences.add(wordCooccurrence);
                }
            }
        }
    }

    private List<String> getStemmedWords(String[] sentences) {
        // FIXME - not stem words that are own names if poor results
        List<String> stemmedWords = new ArrayList<>();
        for (String sentence : sentences) {
            List<String> words = openNlpProvider.filterOutNonWordsAndNouns(sentence);
            for (String word : words) {
                if (notOmittedWord(word))
                stemmedWords.add(openNlpProvider.stemWord(word).toLowerCase());
            }
        }
        return stemmedWords;
    }

    private boolean notOmittedWord(String word) {
        return !word.equals("is");
    }
}

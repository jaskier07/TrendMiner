package pl.kania.trendminer.dataparser.parser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import pl.kania.trendminer.dao.Dao;
import pl.kania.trendminer.dataparser.Tweet;
import pl.kania.trendminer.dataparser.input.TweetAnalysisData;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

@Slf4j
@Component
public class TweetParser {

    private OpenNlpProvider openNlpProvider;
    private Dao dao;
    private Environment environment;

    public TweetParser(@Autowired Dao dao, @Autowired Environment environment, @Autowired OpenNlpProvider openNlpProvider) {
        this.openNlpProvider = openNlpProvider;
        this.dao = dao;
        this.environment = environment;
    }

    public void parseWordsFromTweetsAndFillCooccurrenceTable(TweetAnalysisData tweetAnalysisData) {
        List<Tweet> tweetsInEnglish = tweetAnalysisData.getTweets();

        Duration periodDuration = Duration.of(Long.parseLong(environment.getProperty("pl.kania.period-duration")), ChronoUnit.MINUTES);
        List<AnalysedPeriod> periods = PeriodGenerator.generate(tweetAnalysisData.getStart(), tweetAnalysisData.getEnd(), periodDuration);

        fillCooccurrenceTables(tweetsInEnglish, periods);
        periods.forEach(p -> setSupportValuesAndDropUncommonCooccurrences(tweetsInEnglish, p));

        periods.forEach(p -> dao.saveTimePeriod(p));
    }

    private void fillCooccurrenceTables(List<Tweet> tweetsInEnglish, List<AnalysedPeriod> periods) {
        log.info("Filling cooccurrence tables...");
        for (Tweet tweet : tweetsInEnglish) {
            String[] sentences = openNlpProvider.divideIntoSentences(tweet.getContent());
            List<String> stemmedWords = getStemmedWords(sentences);

            if (stemmedWords.size() > 1) {
                try {
                    tweet.setStemmedWords(Set.copyOf(stemmedWords));
                    AnalysedPeriod currentPeriod = AnalysedPeriod.findPeriodForDate(periods, tweet.getCreatedAt());
                    currentPeriod.incrementDocumentCount();
                    // FIXME shouldn't it be per sentence, not whole tweet content?
                    addWordsToCooccurrenceMap(stemmedWords, currentPeriod);
                } catch (NoSuchElementException e){
                    log.error("Cannot find period", e);
                }
            }
        }

        Integer allCooccurrences = periods.stream()
                .map(p -> p.getCooccurrenceCountPerDocument().size())
                .reduce(Integer::sum)
                .orElseThrow();

        log.info("Done filling cooccurrence tables. Found word cooccurrences: " + allCooccurrences);
    }

    private void setSupportValuesAndDropUncommonCooccurrences(List<Tweet> tweetsInEnglish, AnalysedPeriod period) {
        log.info("Setting support values...");
        Iterator<Map.Entry<WordCooccurrence, Long>> iterator = period.getCooccurrenceCountPerDocument().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<WordCooccurrence, Long> entry = iterator.next();
            WordCooccurrence wordCooccurrence = entry.getKey();
            double support = (double)entry.getValue() / tweetsInEnglish.size();
            if (support < Double.parseDouble(environment.getProperty("pl.kania.support.min-threshold"))) {
                iterator.remove();
                log.debug("Dropped word cooccurrence: " + wordCooccurrence.toString() + " with support = " + support);
            } else {
                wordCooccurrence.setSupport(support);
                log.debug("Preserved word cooccurrence " + wordCooccurrence.toString() + " with support = " + support);
            }
        }
        log.info("Done setting support values. Preserved word cooccurrences: " + period.getCooccurrenceCountPerDocument().size());
    }

    private void addWordsToCooccurrenceMap(List<String> stemmedWords, AnalysedPeriod period) {
        Set<WordCooccurrence> updatedWordCooccurrences = new HashSet<>();

        for (int i = 0; i < stemmedWords.size(); i++) {
            for (int j = i + 1; j < stemmedWords.size(); j++) {
                WordCooccurrence wordCooccurrence = new WordCooccurrence(stemmedWords.get(i), stemmedWords.get(j));
                period.getCooccurrenceCount().merge(wordCooccurrence, 1L, Long::sum);
                if (!updatedWordCooccurrences.contains(wordCooccurrence)) {
                    period.getCooccurrenceCountPerDocument().merge(wordCooccurrence, 1L, Long::sum);
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
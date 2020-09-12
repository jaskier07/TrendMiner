package pl.kania.trendminer.dataparser.parser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import pl.kania.trendminer.util.ProgressLogger;
import pl.kania.trendminer.dao.Dao;
import pl.kania.trendminer.dataparser.Tweet;
import pl.kania.trendminer.dataparser.input.TweetAnalysisData;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Component
public class TweetParser {

    private final OpenNlpProvider openNlpProvider;
    private final Dao dao;
    private final Environment environment;
    private final WordProcessing wordProcessing;
    private final ImproveResults improveResults;

    public TweetParser(@Autowired Dao dao, @Autowired Environment environment, @Autowired OpenNlpProvider openNlpProvider,
                       @Autowired ImproveResults improveResults) {
        this.openNlpProvider = openNlpProvider;
        this.dao = dao;
        this.environment = environment;
        this.improveResults = improveResults;
        this.wordProcessing = new WordProcessing(openNlpProvider, Integer.parseInt(environment.getProperty("pl.kania.min-word-length")), improveResults.get());
    }

    public List<AnalysedPeriod> parseWordsInTweetsAndFillPeriods(TweetAnalysisData tweetAnalysisData) {
        List<AnalysedPeriod> periods = PeriodGenerator.generate(tweetAnalysisData.getStart(), tweetAnalysisData.getEnd(), getDuration());

        fillCooccurrenceTables(tweetAnalysisData.getTweets(), periods);
        setSupportValues(periods);

        return periods;
    }

    private Duration getDuration() {
        long periodStep = Long.parseLong(environment.getProperty("pl.kania.period-duration"));
        int chronoUnitOrdinal = Integer.parseInt(environment.getProperty("pl.kania.period-duration.chrono-unit-ordinal"));
        return Duration.of(periodStep, ChronoUnit.values()[chronoUnitOrdinal]);
    }

    private void setSupportValues(List<AnalysedPeriod> periods) {
        log.info("Setting support values...");
        periods.forEach(this::setSupportValuesAndDropUncommonCooccurrences);
        ProgressLogger.done();
    }

    private void fillCooccurrenceTables(List<Tweet> tweetsInEnglish, List<AnalysedPeriod> periods) {
        log.info("Filling cooccurrence tables...");
        long counter = 0;
        for (Tweet tweet : tweetsInEnglish) {
            String[] sentences = openNlpProvider.divideIntoSentences(tweet.getContent());
            List<String> stemmedWords = wordProcessing.perform(sentences);

            if (stemmedWords.size() > 1) {
                try {
                    AnalysedPeriod currentPeriod = AnalysedPeriod.findPeriodForDate(periods, tweet.getCreatedAt());
                    currentPeriod.incrementDocumentCount();
                    addWordsToCooccurrenceMap(stemmedWords, currentPeriod);
                } catch (NoSuchElementException e) {
                    log.error("Cannot find period", e);
                }
            }
            ProgressLogger.log(counter++, 20000);
        }

        Integer allCooccurrences = periods.stream()
                .map(p -> p.getCooccurrenceCountPerDocument().size())
                .reduce(Integer::sum)
                .orElseThrow();

        ProgressLogger.done("Filling cooccurrence tables. Found word cooccurrences: " + allCooccurrences);
    }

    private void setSupportValuesAndDropUncommonCooccurrences(AnalysedPeriod period) {
        Iterator<Map.Entry<WordCooccurrence, Long>> iterator = period.getCooccurrenceCountPerDocument().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<WordCooccurrence, Long> entry = iterator.next();
            WordCooccurrence wordCooccurrence = entry.getKey();
            double support = (double) entry.getValue() / period.getAllDocumentsCount();

            if (support < Double.parseDouble(environment.getProperty("pl.kania.support.min-threshold"))) {
                iterator.remove();
//                log.debug("Dropped word cooccurrence: " + wordCooccurrence.toString() + " with support = " + support);
            } else {
                wordCooccurrence.setSupport(support);
//                log.debug("Preserved word cooccurrence " + wordCooccurrence.toString() + " with support = " + support);
            }
        }
        ProgressLogger.log(1, 1);
        log.debug("Done setting support values. Preserved word cooccurrences: " + period.getCooccurrenceCountPerDocument().size());
    }

    private void addWordsToCooccurrenceMap(List<String> stemmedWords, AnalysedPeriod period) {
        Set<WordCooccurrence> updatedWordCooccurrences = new HashSet<>();

        for (int i = 0; i < stemmedWords.size(); i++) {
            for (int j = i + 1; j < stemmedWords.size(); j++) {
                WordCooccurrence wordCooccurrence = new WordCooccurrence(stemmedWords.get(i), stemmedWords.get(j));

                // add word only once per document
                if (!updatedWordCooccurrences.contains(wordCooccurrence)) {
                    period.getCooccurrenceCountPerDocument().merge(wordCooccurrence, 1L, Long::sum);
                    updatedWordCooccurrences.add(wordCooccurrence);
                }
            }
        }
    }
}

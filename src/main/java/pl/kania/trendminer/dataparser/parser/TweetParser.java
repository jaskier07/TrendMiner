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
import java.util.stream.Collectors;

@Slf4j
@Component
public class TweetParser {

    private static final String[] WORDS_TO_OMIT = {"is", "a", "an", "the", "i", "we", "you", "rt", "have"};

    private OpenNlpProvider openNlpProvider;
    private Dao dao;
    private Environment environment;
    private final int minWordLength;

    public TweetParser(@Autowired Dao dao, @Autowired Environment environment, @Autowired OpenNlpProvider openNlpProvider) {
        this.openNlpProvider = openNlpProvider;
        this.dao = dao;
        this.environment = environment;
        this.minWordLength = Integer.parseInt(environment.getProperty("pl.kania.min-word-length"));
    }

    public void parseWordsFromTweetsAndFillCooccurrenceTable(TweetAnalysisData tweetAnalysisData) {
        List<Tweet> tweetsInEnglish = tweetAnalysisData.getTweets();

        long periodStep = Long.parseLong(environment.getProperty("pl.kania.period-duration"));
        int chronoUnitOrdinal = Integer.parseInt(environment.getProperty("pl.kania.period-duration.chrono-unit-ordinal"));
        Duration periodDuration = Duration.of(periodStep, ChronoUnit.values()[chronoUnitOrdinal]);
        List<AnalysedPeriod> periods = PeriodGenerator.generate(tweetAnalysisData.getStart(), tweetAnalysisData.getEnd(), periodDuration);

        fillCooccurrenceTables(tweetsInEnglish, periods);
        setSupportValues(periods);

        periods.forEach(p -> dao.saveTimePeriod(p));
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
            List<String> stemmedWords = getLemmatizedWords(sentences);

            if (stemmedWords.size() > 1) {
                try {
                    tweet.setStemmedWords(Set.copyOf(stemmedWords));
                    AnalysedPeriod currentPeriod = AnalysedPeriod.findPeriodForDate(periods, tweet.getCreatedAt());
                    currentPeriod.incrementDocumentCount();
                    addWordsToCooccurrenceMap(stemmedWords, currentPeriod);
                } catch (NoSuchElementException e) {
                    log.error("Cannot find period", e);
                }
            }
            ProgressLogger.log(counter++, 3000);
        }
        ProgressLogger.done();

        Integer allCooccurrences = periods.stream()
                .map(p -> p.getCooccurrenceCountPerDocument().size())
                .reduce(Integer::sum)
                .orElseThrow();

        log.info("Done filling cooccurrence tables. Found word cooccurrences: " + allCooccurrences);
    }

    private void setSupportValuesAndDropUncommonCooccurrences(AnalysedPeriod period) {
        long index = 0;
        Iterator<Map.Entry<WordCooccurrence, Long>> iterator = period.getCooccurrenceCountPerDocument().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<WordCooccurrence, Long> entry = iterator.next();
            WordCooccurrence wordCooccurrence = entry.getKey();
            double support = (double) entry.getValue() / period.getAllDocumentsCount();
            // FIXME perform dropping?
            if (support < Double.parseDouble(environment.getProperty("pl.kania.support.min-threshold"))) {
                iterator.remove();
                log.debug("Dropped word cooccurrence: " + wordCooccurrence.toString() + " with support = " + support);
            } else {
                wordCooccurrence.setSupport(support);
                log.debug("Preserved word cooccurrence " + wordCooccurrence.toString() + " with support = " + support);
            }
            ProgressLogger.log(index++, 10000);
        }
        log.debug("Done setting support values. Preserved word cooccurrences: " + period.getCooccurrenceCountPerDocument().size());
    }

    private void addWordsToCooccurrenceMap(List<String> stemmedWords, AnalysedPeriod period) {
        Set<WordCooccurrence> updatedWordCooccurrences = new HashSet<>();

        for (int i = 0; i < stemmedWords.size(); i++) {
            for (int j = i + 1; j < stemmedWords.size(); j++) {
                WordCooccurrence wordCooccurrence = new WordCooccurrence(stemmedWords.get(i), stemmedWords.get(j));
                if (!updatedWordCooccurrences.contains(wordCooccurrence)) {
                    period.getCooccurrenceCountPerDocument().merge(wordCooccurrence, 1L, Long::sum);
                    updatedWordCooccurrences.add(wordCooccurrence);
                }
            }
        }
    }

    private List<String> getStemmedWords(String[] sentences) {
        List<String> stemmedWords = new ArrayList<>();

        for (String sentence : sentences) {
            List<String> words = openNlpProvider.filterOutNonWordsAndNouns(sentence);
            for (String word : words) {
                if (!wordToOmit(word)) {
                    String stemmedWord = openNlpProvider.stemWord(word).toLowerCase();
                    if (stemmedWord.length() >= minWordLength) {
                        stemmedWords.add(stemmedWord);
                    }
                }
            }
        }
        return stemmedWords;
    }

    private List<String> getLemmatizedWords(String[] sentences) {
        List<String> newWords = new ArrayList<>();

        Arrays.stream(sentences).forEach(sentence -> {
            List<String> words = openNlpProvider.lemmatizeSentence(sentence)
                    .stream()
                    .filter(w -> !wordToOmit(w))
                    .collect(Collectors.toList());
            newWords.addAll(words);
        });

        return newWords;
    }

    private boolean wordToOmit(String word) {
        for (String wordToOmit : WORDS_TO_OMIT) {
            if (word.equals(wordToOmit)) {
                return true;
            }
        }
        return word.length() < minWordLength;
    }
}

package pl.kania.trendminer.parser;

import lombok.extern.slf4j.Slf4j;
import pl.kania.trendminer.input.Tweet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TweetParser {

    private OpenNlpProvider openNlpProvider;

    public TweetParser() {
        this.openNlpProvider = openNlpProvider = new OpenNlpProvider();
    }

    public void parseWordsFromTweetsAndFillCooccurrenceTable(List<Tweet> tweetsInEnglish) {
        Map<WordCooccurrence, Long> cooccurrenceCount = new HashMap<>();
        for (Tweet tweet : tweetsInEnglish) {
            String[] sentences = openNlpProvider.divideIntoSentences(tweet.getContent());
            List<String> stemmedWords = getStemmedWords(sentences);
            // FIXME shouldn't it be per sentence, not whole tweet content?
            addWordsToCooccurrenceMap(stemmedWords, cooccurrenceCount);
        }
    }

    private void addWordsToCooccurrenceMap(List<String> stemmedWords, Map<WordCooccurrence, Long> cooccurrenceCount) {
        for (int i = 0; i < stemmedWords.size(); i++) {
            for (int j = i + 1; j < stemmedWords.size(); j++) {
                WordCooccurrence wordCooccurrence = new WordCooccurrence(stemmedWords.get(i), stemmedWords.get(j));                cooccurrenceCount.merge(wordCooccurrence, 1L, Long::sum);
            }
        }
    }

    private List<String> getStemmedWords(String[] sentences) {
        List<String> stemmedWords = new ArrayList<>();
        for (String sentence : sentences) {
            List<String> words = openNlpProvider.filterOutNonWordsAndNouns(sentence);
            for (String word : words) {
                stemmedWords.add(openNlpProvider.stemWord(word));
            }
        }
        return stemmedWords;
    }
}

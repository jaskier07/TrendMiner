package pl.kania.trendminer.parser;

import lombok.extern.slf4j.Slf4j;
import pl.kania.trendminer.model.Tweet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class TweetParser {

    private OpenNlpProvider openNlpProvider;

    public TweetParser() {
        this.openNlpProvider = openNlpProvider = new OpenNlpProvider();
    }

    public void parseWordsFromTweets(List<Tweet> tweetsInEnglish) {
        for (Tweet tweet : tweetsInEnglish) {
            String[] sentences = openNlpProvider.divideIntoSentences(tweet.getContent());
            for (String sentence : sentences) {
                List<String> words = openNlpProvider.filterOutNonWordsAndNouns(sentence);
                for (String word : words) {
                    tweet.getPreprocessedWords().add(openNlpProvider.stemWord(word));
                }
            }
        }
    }
}

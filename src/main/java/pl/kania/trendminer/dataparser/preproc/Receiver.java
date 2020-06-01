package pl.kania.trendminer.dataparser.preproc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kania.trendminer.dataparser.Tweet;
import pl.kania.trendminer.dataparser.input.TweetProvider;

import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class Receiver {

    private ValidEnglishWordsCounter validEnglishWordsCounter;
    private ValidEnglishWordThresholdProvider validEnglishWordThresholdProvider;

    public Receiver(@Autowired ValidEnglishWordsCounter validEnglishWordsCounter, @Autowired ValidEnglishWordThresholdProvider validEnglishWordThresholdProvider) {
        this.validEnglishWordsCounter = validEnglishWordsCounter;
        this.validEnglishWordThresholdProvider = validEnglishWordThresholdProvider;
    }

    public List<Tweet> getTweetsInEnglish() {
        List<Tweet> tweets = new TweetProvider().getTweets();
        log.info("Tweets found: " + tweets.size());
        filterOutNonEnglishTweets(tweets);
        return tweets;
    }

    private void filterOutNonEnglishTweets(List<Tweet> tweets) {
        Iterator<Tweet> iterator = tweets.iterator();
        while (iterator.hasNext()) {
            Tweet tweet = iterator.next();
            int percentageOfEnglishWords = validEnglishWordsCounter.getPercentageOfEnglishWords(tweet);
            if (percentageOfEnglishWords < validEnglishWordThresholdProvider.getThresholdInPercentage(tweet)) {
                iterator.remove();
                log.info("Removed non-English tweet: " + tweet.getContent());
            }
        }
        log.info("Done filtering non-English tweets.");
    }
}

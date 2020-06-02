package pl.kania.trendminer.dataparser.preproc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.kania.trendminer.dataparser.Tweet;
import pl.kania.trendminer.dataparser.input.TweetAnalysisData;
import pl.kania.trendminer.dataparser.input.TweetProvider;

import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class Receiver {

    private ValidEnglishWordsCounter validEnglishWordsCounter;
    private ValidEnglishWordThresholdProvider validEnglishWordThresholdProvider;
    private Environment environment;

    public Receiver(@Autowired ValidEnglishWordsCounter validEnglishWordsCounter, @Autowired ValidEnglishWordThresholdProvider validEnglishWordThresholdProvider,
                    @Autowired Environment environment) {
        this.validEnglishWordsCounter = validEnglishWordsCounter;
        this.validEnglishWordThresholdProvider = validEnglishWordThresholdProvider;
        this.environment = environment;
    }

    public TweetAnalysisData getTweetsInEnglish() {
        TweetAnalysisData tweetAnalysisData = new TweetProvider().getTweetsAndTweetAnalysisPeriod(environment.getProperty("pl.kania.path.dataset"));
        List<Tweet> tweets = tweetAnalysisData.getTweets();
        log.info("Tweets found: " + tweets.size());
        filterOutNonEnglishTweets(tweets);
        return new TweetAnalysisData(tweets, tweetAnalysisData.getStart(), tweetAnalysisData.getEnd());
    }

    private void filterOutNonEnglishTweets(List<Tweet> tweets) {
        Iterator<Tweet> iterator = tweets.iterator();
        while (iterator.hasNext()) {
            Tweet tweet = iterator.next();
            int percentageOfEnglishWords = validEnglishWordsCounter.getPercentageOfEnglishWords(tweet);
            if (percentageOfEnglishWords < validEnglishWordThresholdProvider.getThresholdInPercentage(tweet)) {
                iterator.remove();
                log.debug("Removed non-English tweet: " + tweet.getContent());
            }
        }
        log.info("Done filtering non-English tweets.");
    }
}

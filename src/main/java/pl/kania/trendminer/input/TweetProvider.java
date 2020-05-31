package pl.kania.trendminer.input;

import pl.kania.trendminer.model.Tweet;
import pl.kania.trendminer.parser.OpenNlpProvider;
import pl.kania.trendminer.preproc.TweetContentTokenizer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TweetProvider {

    private final List<String> exampleWords;
    private static int counter = 0;

    public TweetProvider() {
        exampleWords = TweetContentTokenizer.tokenize("All Twitter APIs that return Tweets provide that data encoded using JavaScript Object Notation (JSON). JSON is based on key-value pairs, with named attributes and associated values. These attributes, and their state are used to describe objects.");
    }

    public List<Tweet> getTweets() {
//        return Stream.generate(this::getExampleTweet).limit(30).collect(Collectors.toList());
        return List.of(getExampleTweet2());
    }

    private Tweet getExampleTweet() {
        StringBuilder content = new StringBuilder("Tweet # ");
        content.append(counter);
        content.append(' ');
        new OpenNlpProvider().divideIntoSentences("ELO. Siema! Co?");

        Random random = new Random();
        for (int i = 0; i < random.nextInt(exampleWords.size()); i++) {
            content.append(exampleWords.get(random.nextInt(exampleWords.size())));
            content.append(' ');
        }

        Tweet tweet = new Tweet(String.valueOf(counter), "en", "Author" + counter, "Poland", content.toString(), LocalDateTime.now());
        counter++;
        return tweet;
    }

    private Tweet getExampleTweet2() {
        return new Tweet(String.valueOf(counter), "en", "Author" + counter, "Poland", "Rupert Murdoch attacked in Parliament during testimony.", LocalDateTime.now());
    }
}

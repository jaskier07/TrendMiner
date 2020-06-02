package pl.kania.trendminer.dataparser.input;

import pl.kania.trendminer.dataparser.Tweet;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TweetProvider {

    private final static List<String> exampleTweetContents = List.of(
            "Rupert Murdoch attacked in Parliament during testimony. Rupert is alright now.",
            "US astronauts Doug Hurley and Bob Behnken have docked with, and entered, the International Space Station (ISS).",
            "Hurley and Behnken launched from Florida on Saturday.",
            "Hurley's and Behnken's job on the mission is to test all onboard systems and to give their feedback to engineers.",
            "Unbelievable!",
            "I'm shocked!",
            "Hurley has done great job!"
    );

    public TweetAnalysisData getTweetsAndTweetAnalysisPeriod(String path) {
        return new CsvReader().readFile(path);
    }

    private List<Tweet> getExampleTweets() {
        return exampleTweetContents.stream()
                .map(t -> new Tweet(UUID.randomUUID().toString(), "en", "Author", "Poland", t, LocalDateTime.now()))
                .collect(Collectors.toList());
    }
}

package pl.kania.trendminer.dataparser.input;

import lombok.Value;
import pl.kania.trendminer.dataparser.Tweet;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Value
public class TweetAnalysisData {
    List<Tweet> tweets;
    LocalDateTime start;
    LocalDateTime end;
}

package pl.kania.trendminer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import pl.kania.trendminer.input.Tweet;
import pl.kania.trendminer.parser.TweetParser;
import pl.kania.trendminer.preproc.Receiver;

import java.util.List;

@Slf4j
@SpringBootApplication
public class TrendMinerApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(TrendMinerApplication.class, args);
		Receiver receiver = applicationContext.getBean(Receiver.class);

		List<Tweet> tweetsInEnglish = receiver.getTweetsInEnglish();
		new TweetParser().parseWordsFromTweetsAndFillCooccurrenceTable(tweetsInEnglish);
	}

}

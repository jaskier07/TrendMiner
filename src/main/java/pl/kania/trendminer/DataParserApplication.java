package pl.kania.trendminer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import pl.kania.trendminer.dataparser.Tweet;
import pl.kania.trendminer.dataparser.input.TweetAnalysisData;
import pl.kania.trendminer.dataparser.parser.TweetParser;
import pl.kania.trendminer.dataparser.preproc.Receiver;

import java.util.List;

@Slf4j
@SpringBootApplication
public class DataParserApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(DataParserApplication.class, args);

		Receiver receiver = applicationContext.getBean(Receiver.class);

		TweetAnalysisData tweetsInEnglish = receiver.getTweetsInEnglish();
		applicationContext.getBean(TweetParser.class).parseWordsFromTweetsAndFillCooccurrenceTable(tweetsInEnglish);

		log.info("App finished.");
	}

}

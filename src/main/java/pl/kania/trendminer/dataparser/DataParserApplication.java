package pl.kania.trendminer.dataparser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import pl.kania.trendminer.dataparser.parser.TweetParser;
import pl.kania.trendminer.dataparser.preproc.Receiver;

import java.util.List;

@Slf4j
@SpringBootApplication
public class DataParserApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(DataParserApplication.class, args);
		Receiver receiver = applicationContext.getBean(Receiver.class);

		List<Tweet> tweetsInEnglish = receiver.getTweetsInEnglish();
		applicationContext.getBean(TweetParser.class).parseWordsFromTweetsAndFillCooccurrenceTable(tweetsInEnglish);

		log.info("App finished.");
	}

}

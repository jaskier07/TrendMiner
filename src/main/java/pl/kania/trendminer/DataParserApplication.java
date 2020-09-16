package pl.kania.trendminer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import pl.kania.trendminer.db.dao.DatabaseService;
import pl.kania.trendminer.dataparser.input.TweetAnalysisData;
import pl.kania.trendminer.dataparser.parser.AnalysedPeriod;
import pl.kania.trendminer.dataparser.parser.TweetParser;
import pl.kania.trendminer.dataparser.parser.preproc.Receiver;
import pl.kania.trendminer.util.TimeDifferenceCounter;

import java.util.List;

@Slf4j
@SpringBootApplication
public class DataParserApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(DataParserApplication.class, args);

		TimeDifferenceCounter tdf = new TimeDifferenceCounter();
		DatabaseService databaseService = applicationContext.getBean(DatabaseService.class);
		databaseService.deleteAll();

		tdf.start();
		Receiver receiver = applicationContext.getBean(Receiver.class);
		TweetAnalysisData tweetsInEnglish = receiver.getTweetsInEnglish();
		List<AnalysedPeriod> periods = applicationContext.getBean(TweetParser.class).parseWordsInTweetsAndFillPeriods(tweetsInEnglish);

		tdf.stop();
		databaseService.saveAllPeriods(periods);

		log.info("Overall difference: " + tdf.getDifference());
		log.info("App finished.");
	}

}

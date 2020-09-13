package pl.kania.trendminer.dataparser.input.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import pl.kania.trendminer.QueryProcessorApplication;
import pl.kania.trendminer.dataparser.input.FileOutputProvider;
import pl.kania.trendminer.dataparser.parser.DurationProvider;
import pl.kania.trendminer.dataparser.parser.ImproveResults;
import pl.kania.trendminer.db.model.Word;
import pl.kania.trendminer.queryprocessor.TimeIdProvider;
import pl.kania.trendminer.queryprocessor.cluster.FrequentlyOccurringClusterIdentifier;
import pl.kania.trendminer.queryprocessor.cluster.model.Cluster;
import pl.kania.trendminer.queryprocessor.cluster.trending.TrendingClusterResult;
import pl.kania.trendminer.queryprocessor.result.ResultPrinter;
import pl.kania.trendminer.util.NumberFormatter;

import java.io.FileInputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootApplication(scanBasePackages = "pl.kania")
public class QueryProcessorToFileApplication {

    public static void main(String... args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(QueryProcessorToFileApplication.class, args);
        DurationProvider durationProvider = ctx.getBean(DurationProvider.class);
        TimeIdProvider timeIdProvider = ctx.getBean(TimeIdProvider.class);

        Environment environment = ctx.getBean(Environment.class);
        String pathToOutput = environment.getProperty("pl.kania.path.path-output");
        String datePattern = environment.getProperty("pl.kania.time.date-time-pattern");
        String from = environment.getProperty("pl.kania.time.period-from");
        String to = environment.getProperty("pl.kania.time.period-to");
        int periodStartIndex = Integer.parseInt(environment.getProperty("pl.kania.time.file.period-index-start"));
        int periodStopIndex = Integer.parseInt(environment.getProperty("pl.kania.time.file.period-index-stop"));
        int previousPeriods = Integer.parseInt(environment.getProperty("pl.kania.time.previous-periods-to-analyse"));

        FileOutputProvider fop = new FileOutputProvider(pathToOutput);
        QueryResultWriter writer = new QueryResultWriter(fop);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(datePattern);
        LocalDateTime initialStart = LocalDateTime.from(dtf.parse(from));
        LocalDateTime start = initialStart;

        for (int periodIndex = periodStartIndex; periodIndex < periodStopIndex; periodIndex++) {
            start = start.plus(durationProvider.getDuration());
            timeIdProvider.init(initialStart, start, periodIndex, previousPeriods);

            FrequentlyOccurringClusterIdentifier identifier = ctx.getBean(FrequentlyOccurringClusterIdentifier.class);
            List<TrendingClusterResult> results = identifier.identify(timeIdProvider);
            List<Cluster> sortedResults = ResultPrinter.getSortedResults(results, 10);

            for (Cluster cluster : sortedResults) {
                writer.appendText(periodIndex + "," + timeIdProvider.getStart() + "," + timeIdProvider.getEnd() + ","
                        + NumberFormatter.format(cluster.getBurstiness(), 5) + ","
                        + cluster.getWords().stream().map(Word::getWord).collect(Collectors.joining(",")) + "\n");
            }

            Duration durationFromInitialStart = Duration.between(initialStart, start);
            Duration durationBetweenAnalysedPeriods = durationProvider.getDuration().multipliedBy(previousPeriods);
            if (durationFromInitialStart.compareTo(durationBetweenAnalysedPeriods) >= 0) {
                initialStart = initialStart.plus(durationProvider.getDuration());
            }

        }

        log.info("Writing to file...");
        writer.write();
        log.info("Done.");
    }
}

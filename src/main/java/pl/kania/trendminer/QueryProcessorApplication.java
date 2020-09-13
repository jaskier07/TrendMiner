package pl.kania.trendminer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import pl.kania.trendminer.dataparser.parser.ImproveResults;
import pl.kania.trendminer.queryprocessor.TimeIdProvider;
import pl.kania.trendminer.queryprocessor.cluster.FrequentlyOccurringClusterIdentifier;
import pl.kania.trendminer.queryprocessor.cluster.trending.TrendingClusterResult;
import pl.kania.trendminer.queryprocessor.result.ResultPrinter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@SpringBootApplication
public class QueryProcessorApplication {

    public static void main(String... args) {
        ApplicationContext applicationContext = SpringApplication.run(QueryProcessorApplication.class, args);
        ImproveResults improveResults = applicationContext.getBean(ImproveResults.class);

        Environment environment = applicationContext.getBean(Environment.class);
        String datePattern = environment.getProperty("pl.kania.time.date-time-pattern");
        String from = environment.getProperty("pl.kania.time.period-from");
        String to = environment.getProperty("pl.kania.time.period-to");
        int periodStartIndex = Integer.parseInt(environment.getProperty("pl.kania.time.period-index-start"));
        int previousPeriods = Integer.parseInt(environment.getProperty("pl.kania.time.previous-periods-to-analyse"));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(datePattern);
        LocalDateTime start = LocalDateTime.from(dtf.parse(from));
        LocalDateTime end = LocalDateTime.from(dtf.parse(to));

        TimeIdProvider timeIdProvider = applicationContext.getBean(TimeIdProvider.class);
        timeIdProvider.init(start, end, periodStartIndex, previousPeriods);

        FrequentlyOccurringClusterIdentifier identifier = applicationContext.getBean(FrequentlyOccurringClusterIdentifier.class);
        List<TrendingClusterResult> results = identifier.identify(timeIdProvider);
        ResultPrinter.sortAndPrintResults(results, improveResults.get());
    }
}

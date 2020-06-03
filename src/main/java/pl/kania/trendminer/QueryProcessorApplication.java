package pl.kania.trendminer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import pl.kania.trendminer.dao.TimeIDDao;
import pl.kania.trendminer.model.TimeId;
import pl.kania.trendminer.queryprocessor.cluster.FrequentlyOccurringClusterIdentifier;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@SpringBootApplication
public class QueryProcessorApplication {

    public static void main(String... args) {
        ApplicationContext applicationContext = SpringApplication.run(QueryProcessorApplication.class, args);
        Environment environment = applicationContext.getBean(Environment.class);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(environment.getProperty("pl.kania.time.date-time-patter"));
        LocalDateTime start = LocalDateTime.from(dtf.parse(environment.getProperty("pl.kania.time.period-from")));
        LocalDateTime end = LocalDateTime.from(dtf.parse(environment.getProperty("pl.kania.time.period-to")));

        TimeIDDao timeIdDao = applicationContext.getBean(TimeIDDao.class);

        List<TimeId> all = timeIdDao.findAll();
        // TODO time zone
//        .stream()
//                .filter(d -> d.getStartTime().isAfter(start) && d.getEndTime().isBefore(end))
//                .collect(Collectors.toList())
//
//        List<TimeId> timeIds = all.subList(18, all.size() - 1);//timeIdDao.findByStartTimeAfterAndEndTimeBefore(start, end);
        applicationContext.getBean(FrequentlyOccurringClusterIdentifier.class).identify(all);
    }
}

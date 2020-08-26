package pl.kania.trendminer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import pl.kania.trendminer.queryprocessor.cluster.FrequentlyOccurringClusterIdentifier;

@Slf4j
@SpringBootApplication
public class QueryProcessorApplication {

    public static void main(String... args) {
        ApplicationContext applicationContext = SpringApplication.run(QueryProcessorApplication.class, args);
        applicationContext.getBean(FrequentlyOccurringClusterIdentifier.class).identify();
    }
}

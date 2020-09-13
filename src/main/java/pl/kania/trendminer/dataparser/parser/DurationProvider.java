package pl.kania.trendminer.dataparser.parser;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Service
public class DurationProvider {

    @Getter
    private final Duration duration;

    public DurationProvider(@Autowired Environment environment) {
        long periodStep = Long.parseLong(environment.getProperty("pl.kania.period-duration"));
        int chronoUnitOrdinal = Integer.parseInt(environment.getProperty("pl.kania.period-duration.chrono-unit-ordinal"));
        this.duration = Duration.of(periodStep, ChronoUnit.values()[chronoUnitOrdinal]);
    }
}

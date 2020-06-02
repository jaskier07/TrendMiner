package pl.kania.trendminer.dataparser.parser;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PeriodGenerator {

    public static List<AnalysedPeriod> generate(LocalDateTime start, LocalDateTime end, Duration periodDuration) {
        List<AnalysedPeriod> periods = new ArrayList<>();

        LocalDateTime periodStart = start;
        LocalDateTime periodEnd;

        while (periodStart.isBefore(end)) {
            periodEnd = periodStart.plus(periodDuration);
            AnalysedPeriod newPeriod = new AnalysedPeriod(periodStart, periodEnd);
            periods.add(newPeriod);
            periodStart = periodEnd;

            log.info("Period created: " + newPeriod.toString());
        }

        log.info("Created " + periods.size() + " periods.");

        return periods;
    }
}

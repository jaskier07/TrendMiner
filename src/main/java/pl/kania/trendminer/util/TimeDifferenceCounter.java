package pl.kania.trendminer.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TimeDifferenceCounter {
    private Duration duration = Duration.ZERO;

    private LocalDateTime start;
    private LocalDateTime end;

    public void start() {
        start = LocalDateTime.now();
    }

    public void stop() {
        end = LocalDateTime.now();
        duration = duration.plus(Duration.between(start, end));
    }

    public String getDifference() {
        return "Time in seconds = " + duration.toSeconds() + "s";
    }

    public Long getDifferenceInMillis() {
        return duration.toMillis();
    }

}



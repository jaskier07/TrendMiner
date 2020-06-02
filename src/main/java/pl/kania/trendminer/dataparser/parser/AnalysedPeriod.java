package pl.kania.trendminer.dataparser.parser;


import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@JBossLog
@RequiredArgsConstructor
@Getter
public class AnalysedPeriod {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final @NonNull LocalDateTime start;
    private final @NonNull LocalDateTime end;
    private final Map<WordCooccurrence, Long> cooccurrenceCount = new HashMap<>();
    private final Map<WordCooccurrence, Long> cooccurrenceCountPerDocument = new HashMap<>();
    private long allDocumentsCount;

    boolean isDateInPeriod(LocalDateTime date) {
        return (date.isBefore(end) && date.isAfter(start)) || (date.isEqual(end) || date.isEqual(start));
    }

    void incrementDocumentCount() {
        allDocumentsCount++;
    }

    @Override
    public String toString() {
        return "[" + DATE_FORMATTER.format(start) + ", " + DATE_FORMATTER.format(end) + "]";
    }

    static AnalysedPeriod findPeriodForDate(List<AnalysedPeriod> periods, LocalDateTime date) {
        return periods.stream()
                .filter(p -> p.isDateInPeriod(date))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Cannot find period for date: " + DATE_FORMATTER.format(date)));
    }
}

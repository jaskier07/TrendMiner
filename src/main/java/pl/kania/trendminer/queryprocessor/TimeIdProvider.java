package pl.kania.trendminer.queryprocessor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kania.trendminer.db.dao.TimeIdDao;
import pl.kania.trendminer.db.model.TimeId;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TimeIdProvider {

    private final TimeIdDao timeIdDao;
    @Getter
    private LocalDateTime start;
    @Getter
    private LocalDateTime end;

    @Getter
    private final List<TimeId> allTimeIds;
    @Getter
    private final List<TimeId> timeIdsInRange;

    public TimeIdProvider(@Autowired TimeIdDao timeIDDao) {
        this.timeIdDao = timeIDDao;
        allTimeIds = new ArrayList<>();
        timeIdsInRange = new ArrayList<>();
    }

    public void init(String datePattern, String periodFrom, String periodTo) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(datePattern);
        start = LocalDateTime.from(dtf.parse(periodFrom));
        end = LocalDateTime.from(dtf.parse(periodTo));

        timeIdsInRange.clear();
        allTimeIds.clear();

        timeIdsInRange.addAll(timeIdDao.findAllByStartTimeAfterAndEndTimeBefore(start, end));
        allTimeIds.addAll(timeIdDao.findAll());
    }

    public void init(LocalDateTime periodFrom, LocalDateTime periodTo, int index, int numPreviousPeriods) {
        timeIdsInRange.clear();
        allTimeIds.clear();

        this.start = periodFrom;
        this.end = periodTo;

        timeIdsInRange.addAll(timeIdDao.findAllByIndex(index));
        allTimeIds.addAll(timeIdDao.findAllByIndexGreaterThanAndIndexLessThan(index - numPreviousPeriods - 1, index + 1));

        timeIdsInRange.stream()
                .filter(t -> t.getIndex() == index)
                .findFirst()
                .ifPresent(p -> log.info("Chosen period: " + p.toString()));
    }

    public int getPeriodsBeforeUserStart() {
        return Long.valueOf(getAllTimeIds().stream()
                .filter(p -> p.getStartTime().isBefore(start))
                .count())
                .intValue();
    }
}

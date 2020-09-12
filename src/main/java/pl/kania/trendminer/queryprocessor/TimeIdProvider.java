package pl.kania.trendminer.queryprocessor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.kania.trendminer.dao.TimeIdDao;
import pl.kania.trendminer.model.TimeId;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class TimeIdProvider {

    private final TimeIdDao timeIdDao;
    private final LocalDateTime start;
    private final LocalDateTime end;

    @Getter
    private final List<TimeId> allTimeIds;
    @Getter
    private final List<TimeId> timeIdsInRange;

    public TimeIdProvider(@Autowired TimeIdDao timeIDDao, @Autowired Environment environment) {
        this.timeIdDao = timeIDDao;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(environment.getProperty("pl.kania.time.date-time-patter"));
        start = LocalDateTime.from(dtf.parse(environment.getProperty("pl.kania.time.period-from")));
        end = LocalDateTime.from(dtf.parse(environment.getProperty("pl.kania.time.period-to")));

        if (chooseByIndex(environment)) {
            int index = Integer.parseInt(environment.getProperty("pl.kania.time.period-index-start"));
            int numPreviousPeriods = Integer.parseInt(environment.getProperty("pl.kania.time.previous-periods-to-analyse"));
            allTimeIds = timeIDDao.findAllByIndexGreaterThan(index - numPreviousPeriods - 1);
            timeIdsInRange = timeIDDao.findAllByIndex(index);

            timeIdsInRange.stream().filter(t -> t.getIndex() == index).findFirst().ifPresent(p -> log.info("Chosen period: " + p.toString()));
        } else {
            timeIdsInRange = timeIdDao.findAllByStartTimeAfterAndEndTimeBefore(start, end);
            allTimeIds = timeIdDao.findAll();
        }
    }

    private boolean chooseByIndex(Environment environment) {
        String property = environment.getProperty("pl.kania.time.period-index-start");
        return property != null;
    }

    public int getPeriodsBeforeUserStart() {
        return Long.valueOf(getAllTimeIds().stream()
                .filter(p -> p.getStartTime().isBefore(start))
                .count()).intValue();
    }

    public void get() {

    }
}

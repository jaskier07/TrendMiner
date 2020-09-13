package pl.kania.trendminer.db.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kania.trendminer.db.model.TimeId;

import java.time.LocalDateTime;
import java.util.List;

public interface TimeIdDao extends JpaRepository<TimeId, Long> {

    List<TimeId> findAllByStartTimeAfterAndEndTimeBefore(LocalDateTime startTime, LocalDateTime endTime);

    List<TimeId> findAllByIndexGreaterThanAndIndexLessThan(int greater, int lower);

    List<TimeId> findAllByIndex(int index);
}

package pl.kania.trendminer.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kania.trendminer.model.TimeId;

import java.time.LocalDateTime;
import java.util.List;

public interface TimeIDDao extends JpaRepository<TimeId, Long> {
    List<TimeId> findByStartTimeAfterAndEndTimeBefore(LocalDateTime startTime, LocalDateTime endTime);
}

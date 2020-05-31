package pl.kania.trendminer.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kania.trendminer.model.TimeID;

public interface TimeIDDao extends JpaRepository<TimeID, Long> {
}

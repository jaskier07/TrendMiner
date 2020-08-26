package pl.kania.trendminer.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kania.trendminer.model.Cooccurrence;
import pl.kania.trendminer.model.TimeId;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface CooccurrenceDao extends JpaRepository<Cooccurrence, Long> {

    List<Cooccurrence> findAllByTimeIDId(Long timeId);

    List<Cooccurrence> findAllByIdIn(Set<Long> ids);

}

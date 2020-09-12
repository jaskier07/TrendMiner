package pl.kania.trendminer.db.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kania.trendminer.db.model.Cooccurrence;

import java.util.List;
import java.util.Set;

public interface CooccurrenceDao extends JpaRepository<Cooccurrence, Long> {

    List<Cooccurrence> findAllByTimeIDId(Long timeId);

    List<Cooccurrence> findAllByIdIn(Set<Long> ids);

}

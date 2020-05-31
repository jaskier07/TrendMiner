package pl.kania.trendminer.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kania.trendminer.model.Cooccurrence;

import java.util.Optional;

public interface CooccurrenceDao extends JpaRepository<Cooccurrence, Long> {

    Optional<Cooccurrence> findFirstByWord1AndWord2AndTimeID(Long word1, Long word2, Long timeId);
}

package pl.kania.trendminer.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kania.trendminer.model.Word;

import java.util.Optional;

public interface WordDao extends JpaRepository<Word, Long> {

    Optional<Word> findFirstByWordEquals(String word);
}

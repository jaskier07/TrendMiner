package pl.kania.trendminer.queryprocessor.cluster.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Value;
import pl.kania.trendminer.model.Cooccurrence;
import pl.kania.trendminer.model.Word;

import java.util.List;
import java.util.Objects;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CooccurrenceAllPeriods {
    @EqualsAndHashCode.Include
    private final Word word1;
    @EqualsAndHashCode.Include
    private final Word word2;
    @Setter
    private Double support;

    public CooccurrenceAllPeriods(Cooccurrence cooccurrence) {
        this(cooccurrence.getWord1(), cooccurrence.getWord2());
    }

    public CooccurrenceAllPeriods(Word word1, Word word2) {
        Objects.requireNonNull(word1.getId());
        Objects.requireNonNull(word2.getId());

        if (word1.compareTo(word2) > 0) {
            this.word1 = word2;
            this.word2 = word1;
        } else {
            this.word1 = word1;
            this.word2 = word2;
        }
    }

    public List<Word> getWords() {
        return List.of(word1, word2);
    }
}

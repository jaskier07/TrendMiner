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

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CooccurrenceAllPeriods {
    @EqualsAndHashCode.Include
    private final String word1;
    @EqualsAndHashCode.Include
    private final String word2;
    @Setter
    private Double support;

    public CooccurrenceAllPeriods(Cooccurrence cooccurrence) {
        this.word1 = cooccurrence.getWord1().getWord();
        this.word2 = cooccurrence.getWord2().getWord();
    }

    public CooccurrenceAllPeriods(String word1, String word2) {
        if (word1.compareTo(word2) < 0) {
            this.word1 = word1;
            this.word2 = word2;
        } else {
            this.word1 = word2;
            this.word2 = word1;
        }
    }

    public List<String> getWords() {
        return List.of(word1, word2);
    }
}

package pl.kania.trendminer.dataparser.parser;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class WordCooccurrence {
    @EqualsAndHashCode.Include
    private final String word1;
    @EqualsAndHashCode.Include
    private final String word2;
    private Double support;

    public WordCooccurrence(String word1, String word2) {
        if (word1.compareTo(word2) < 0) {
            this.word1 = word1;
            this.word2 = word2;
        } else {
            this.word1 = word2;
            this.word2 = word1;
        }
    }

    public WordCooccurrence(String word1, String word2, double support) {
        this(word1, word2);
        this.support = support;
    }

    @Override
    public String toString() {
        return "[" + word1 + ", " + word2 + "]";
    }
}

package pl.kania.trendminer.dataparser.parser;

import lombok.Data;

@Data
public class WordCooccurrence {
    private final String word1;
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

    @Override
    public String toString() {
        return "[" + word1 + ", " + word2 + "]";
    }
}

package pl.kania.trendminer.dataparser.parser;

import lombok.Data;

@Data
public class WordCooccurrence {
    private final String word1;
    private final String word2;
    private Double support;

    @Override
    public String toString() {
        return "[" + word1 + ", " + word2 + "]";
    }
}

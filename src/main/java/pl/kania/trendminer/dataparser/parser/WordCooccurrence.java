package pl.kania.trendminer.dataparser.parser;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

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

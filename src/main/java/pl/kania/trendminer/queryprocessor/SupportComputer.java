package pl.kania.trendminer.queryprocessor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.kania.trendminer.dataparser.parser.WordCooccurrence;

public class SupportComputer {

    private final double threshold;

    public SupportComputer(double threshold) {
        this.threshold = threshold;
    }

    public boolean meetsThreshold(long wordFrequency, long totalFrequency) {
        return meetsThreshold(countSupport(wordFrequency, totalFrequency));
    }

    public boolean meetsThreshold(double support) {
        return support >= threshold;
    }

    public double countSupport(long wordFrequency, long totalFrequency) {
        return (double)wordFrequency / totalFrequency;
    }
}

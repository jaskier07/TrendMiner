package pl.kania.trendminer.queryprocessor.cluster.trending;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AverageFrequencyFactory {

    private static final AverageFrequency averageFrequency;
    static {
        averageFrequency = new AverageFrequency();
        averageFrequency.addFrequency(8L);
        averageFrequency.addFrequency(4L);
        averageFrequency.addFrequency(3L);
        averageFrequency.addFrequency(9L);
        averageFrequency.addFrequency(11L);
    }

    public static AverageFrequency getFrequency() {
        return averageFrequency;
    }

    public static int getPeriodsBeforeUserStart() {
        return 3;
    }
}

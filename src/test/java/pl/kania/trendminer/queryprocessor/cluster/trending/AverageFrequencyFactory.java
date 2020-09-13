package pl.kania.trendminer.queryprocessor.cluster.trending;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AverageFrequencyFactory {

    private static final AverageFrequency AVERAGE_FREQUENCY_1;
    private static final AverageFrequency AVERAGE_FREQUENCY_2;
    static {
        AVERAGE_FREQUENCY_1 = new AverageFrequency(getPeriodsBeforeUserStart());
        AVERAGE_FREQUENCY_1.addFrequency(8L);
        AVERAGE_FREQUENCY_1.addFrequency(4L);
        AVERAGE_FREQUENCY_1.addFrequency(3L);
        AVERAGE_FREQUENCY_1.addFrequency(9L);
        AVERAGE_FREQUENCY_1.addFrequency(11L);

        AVERAGE_FREQUENCY_2 = new AverageFrequency(3);
        AVERAGE_FREQUENCY_2.addFrequency(2L);
        AVERAGE_FREQUENCY_2.addFrequency(2L);
        AVERAGE_FREQUENCY_2.addFrequency(5L);
        AVERAGE_FREQUENCY_2.addFrequency(13L);
        AVERAGE_FREQUENCY_2.addFrequency(25L);
    }

    public static AverageFrequency getFrequencyExample1() {
        return AVERAGE_FREQUENCY_1;
    }

    public static AverageFrequency getFrequencyExample2() {
        return AVERAGE_FREQUENCY_2;
    }

    public static int getPeriodsBeforeUserStart() {
        return 3;
    }
}

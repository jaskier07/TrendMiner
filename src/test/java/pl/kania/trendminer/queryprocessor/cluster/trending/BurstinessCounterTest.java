package pl.kania.trendminer.queryprocessor.cluster.trending;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BurstinessCounterTest {

    @Test
    void givenFrequencyFromExample1ValuesCountBurstiness() {
        AverageFrequency average = AverageFrequencyFactory.getFrequencyExample1();
        double burstiness = new BurstinessCounter().count(average);
        Assertions.assertEquals(1.5, burstiness, 0.1);
    }

    @Test
    void givenFrequencyFromExample2ValuesCountBurstiness() {
        AverageFrequency average = AverageFrequencyFactory.getFrequencyExample2();
        double burstiness = new BurstinessCounter().count(average);
        Assertions.assertEquals(6 + 6./7, burstiness, 0.1);
    }
}
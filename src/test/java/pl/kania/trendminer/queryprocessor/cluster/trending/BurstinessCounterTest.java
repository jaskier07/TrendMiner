package pl.kania.trendminer.queryprocessor.cluster.trending;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class BurstinessCounterTest {

    @Test
    void givenFrequencyValuesCountBurstiness() {
        AverageFrequency average = AverageFrequencyFactory.getFrequency();
        double burstiness = new BurstinessCounter().count(average, AverageFrequencyFactory.getPeriodsBeforeUserStart());
        double expected = 1 + 1./38;
        Assertions.assertEquals(expected, burstiness, 0.1);
    }
}
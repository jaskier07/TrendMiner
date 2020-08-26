package pl.kania.trendminer.queryprocessor.cluster.trending;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class AverageFrequencyTest {

    @Test
    void addingFrequenciesExpectCountingAverageFrequencies() {
        AverageFrequency averageFrequency = AverageFrequencyFactory.getFrequency();
        List<Double> expectedAverage = List.of(8., 6., 5., 6., 7.);
        Assertions.assertEquals(expectedAverage, averageFrequency.getAverage());
    }

}
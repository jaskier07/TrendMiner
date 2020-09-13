package pl.kania.trendminer.queryprocessor.cluster.trending;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class AverageFrequencyTest {

    @Test
    void addingFrequenciesFromExample1ExpectCountingAverageFrequencies() {
        AverageFrequency averageFrequency = AverageFrequencyFactory.getFrequencyExample1();

        List<Double> average = averageFrequency.getAverageBefore();
        average.addAll(averageFrequency.getAverageAfter());

        List<Double> expectedAverage = List.of(8., 6., 5., 9., 10.);
        Assertions.assertEquals(expectedAverage, average);
    }

    @Test
    void addingFrequenciesFromExample2ExpectCountingAverageFrequencies() {
        AverageFrequency avg = AverageFrequencyFactory.getFrequencyExample2();
        List<Double> average = avg.getAverageBefore();
        average.addAll(avg.getAverageAfter());

        List<Double> expectedAvg = List.of(2., 2., 3., 13., 19.);

        Assertions.assertEquals(expectedAvg, average);
    }

}
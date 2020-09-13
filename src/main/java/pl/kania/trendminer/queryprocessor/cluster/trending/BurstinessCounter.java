package pl.kania.trendminer.queryprocessor.cluster.trending;

import java.util.List;

public class BurstinessCounter {

    public double count(AverageFrequency averageFrequency) {
        double averageAfter = getAverage(averageFrequency.getAverageAfter());
        double averageBefore = getAverage(averageFrequency.getAverageBefore());

        if (averageBefore == 0.) {
            return averageAfter;
        }
        return averageAfter / averageBefore;
    }

    private double getAverage(List<Double> frequencies) {
        if (frequencies.isEmpty()) {
            return 0;
        }
        return frequencies.stream()
                .reduce(Double::sum)
                .orElseThrow(() -> new IllegalStateException("Error summing up average frequencies")) / frequencies.size();
    }
}
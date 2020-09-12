package pl.kania.trendminer.queryprocessor.cluster.trending;

import java.util.List;

public class BurstinessCounter {

    public double count(AverageFrequency averageFrequency, int periodsBeforeUserStart) {
        List<Double> average = averageFrequency.getAverage();

        double averageAfter = getAverage(average.subList(periodsBeforeUserStart, average.size()));
        double averageBefore = getAverage(average.subList(0, periodsBeforeUserStart));

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
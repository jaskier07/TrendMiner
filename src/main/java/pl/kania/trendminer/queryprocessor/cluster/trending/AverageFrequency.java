package pl.kania.trendminer.queryprocessor.cluster.trending;


import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

class AverageFrequency {
    private List<Long> frequencies = new ArrayList<>();
    private List<Double> averageFrequencies = new ArrayList<>();

    void addFrequency(Long frequency) {
        int periods = frequencies.size();

        if (periods > 0) {
            averageFrequencies.add(countAverage(periods));
        }

        frequencies.add(frequency);
    }

    private double countAverage(int periods) {
        double fraction = 1. / periods;
        Long sum = sumFrequencies();
        return fraction * sum;
    }

    List<Double> getAverage() {
        List<Double> average = new ArrayList<>(averageFrequencies);
        average.add(countAverage(frequencies.size()));
        return average;
    }

    private Long sumFrequencies() {
        return frequencies.stream()
                .reduce(Long::sum)
                .orElseThrow(() -> new IllegalStateException("Error summing up frequencies"));
    }
}


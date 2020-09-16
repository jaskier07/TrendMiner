package pl.kania.trendminer.queryprocessor.cluster.trending;


import java.util.ArrayList;
import java.util.List;

class AverageFrequency {
    private final List<Double> frequenciesBefore = new ArrayList<>();
    private final List<Double> frequenciesAfter = new ArrayList<>();

    private final List<Double> averageFrequenciesBefore = new ArrayList<>();
    private final List<Double> averageFrequenciesAfter = new ArrayList<>();

    private final int periodsBefore;

    public AverageFrequency(int periodsBefore) {
        this.periodsBefore = periodsBefore;
    }

    void addFrequency(Long frequency) {
        if (frequenciesBefore.size() < periodsBefore) {
            if (!frequenciesBefore.isEmpty()) {
                averageFrequenciesBefore.add(countAverage(frequenciesBefore));
            }
            frequenciesBefore.add(frequency.doubleValue());
        } else {
            if (!frequenciesAfter.isEmpty()) {
                averageFrequenciesAfter.add(countAverage(frequenciesAfter));
            }
            frequenciesAfter.add(frequency.doubleValue());
        }
    }

    List<Double> getAverageBefore() {
        if (averageFrequenciesBefore.isEmpty()) {
            return List.of(1.);
        }
        List<Double> average = new ArrayList<>(averageFrequenciesBefore);
        average.add(countAverage(frequenciesBefore));
        return average;
    }

    List<Double> getAverageAfter() {
        List<Double> average = new ArrayList<>(averageFrequenciesAfter);
        average.add(countAverage(frequenciesAfter));
        return average;
    }


    private <T extends Number>double countAverage(List<T> elementsToCount) {
        double fraction = 1. / elementsToCount.size();
        Double sum = sumFrequencies(elementsToCount);
        return fraction * sum;
    }

    private <T extends Number>Double sumFrequencies(List<T> elementsToCount) {
        if (elementsToCount.isEmpty()) {
            return 0.;
        }
        return elementsToCount.stream()
                .map(T::doubleValue)
                .reduce(Double::sum)
                .orElseThrow(() -> new IllegalStateException("Error summing up frequencies"));
    }
}


package pl.kania.trendminer.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AverageCounter {

    public static double count(List<Double> value) {
        int size = value.size();
        return value.stream()
                .reduce(Double::sum)
                .orElse(0.) / size;

    }
}


package pl.kania.trendminer.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NumberFormatter {

    public static <T extends Number> String formatPercentage(T value, T max) {
        return " (" + String.format("%.2f", 100. * value.doubleValue() / max.doubleValue()) + "%) ";
    }

    public static <T extends Number> String format(T value) {
        return format(value, 2);
    }

    public static <T extends Number> String format(T value, int precision) {
        return String.format("%." + precision + "f", value.doubleValue());
    }
}

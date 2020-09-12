package pl.kania.trendminer.util;


import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Counter {
    @Getter
    private int value;

    public void increment() {
        value++;
    }

    public Counter(int startValue) {
        this.value = startValue;
    }

    public int getValueAndIncrement() {
        int val = value;
        increment();
        return val;
    }

    public String getValueAsStringAndIncrement() {
        return Integer.toString(getValueAndIncrement());
    }
}


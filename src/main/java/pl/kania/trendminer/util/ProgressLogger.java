package pl.kania.trendminer.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProgressLogger {

    public static void log(long index) {
        log(index, 5000);
    }

    public static void log(long index, int threshold) {
        if (index % threshold == 0) {
            System.out.print('.');
        }
    }

    public static void done() {
        System.out.println("Done.");
    }
}

package pl.kania.trendminer.queryprocessor.cluster.trending;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import pl.kania.trendminer.dataparser.parser.WordCooccurrence;
import pl.kania.trendminer.model.TimeId;
import pl.kania.trendminer.queryprocessor.cluster.model.Cluster;
import pl.kania.trendminer.util.ProgressLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BurstinessCounter {

    public static Result getBurstiness(Cluster cluster, Map<TimeId, Map<WordCooccurrence, Double>> allCooccurrencesPerId) {
        List<Double> frequencies = new ArrayList<>();

        long notFoundCounter = 0;
        long totalCount = 0;
        for (Map.Entry<TimeId, Map<WordCooccurrence, Double>> supportPerTimeId : allCooccurrencesPerId.entrySet()) {
            double minFrequencyInPeriod = Double.MAX_VALUE;

            for (int i = 0; i < cluster.getWords().size(); i++) {
                String word1 = cluster.getWords().get(i);
                for (int j = i; j < cluster.getWords().size(); j++) {
                    String word2 = cluster.getWords().get(j);
                    WordCooccurrence cooccurrence = new WordCooccurrence(word1, word2);
                    Double frequency = supportPerTimeId.getValue().get(cooccurrence);
                    if (frequency == null) {
//                        log.warn("lack of cooccurrence: " + cooccurrence.toString() + ", timeid=" + supportPerTimeId.getKey().toString());
                        notFoundCounter++;
                    } else {
                        minFrequencyInPeriod = Math.min(frequency, minFrequencyInPeriod);
                    }
                    totalCount++;
                }
            }

            if (minFrequencyInPeriod == Double.MAX_VALUE) {
//                log.warn("Lack of any cooccurrence in cluster: " + cluster.toString());
            }
            frequencies.add(minFrequencyInPeriod);
        }

        double firstFrequency = frequencies.get(0);
        double sumFrequencies = frequencies.stream()
                .reduce(Double::sum)
                .orElseThrow();
        int numPeriods = allCooccurrencesPerId.size();
        double avgFrequency = sumFrequencies / numPeriods;
        return new Result(firstFrequency / avgFrequency, notFoundCounter, totalCount);
    }

    @Value
    static class Result {
        double burstiness;
        long notFoundCount;
        long totalCount;
    }
}

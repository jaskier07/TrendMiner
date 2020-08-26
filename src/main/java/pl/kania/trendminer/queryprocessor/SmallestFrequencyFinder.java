package pl.kania.trendminer.queryprocessor;

import lombok.Data;
import lombok.Value;
import pl.kania.trendminer.model.Cooccurrence;
import pl.kania.trendminer.model.TimeId;
import pl.kania.trendminer.queryprocessor.cluster.model.CooccurrenceAllPeriods;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SmallestFrequencyFinder {

    public long findInTimeId(Set<CooccurrenceAllPeriods> clusterCooccurrences, TimeId timeId, Map<TimeId, Map<Cooccurrence, Cooccurrence>> allCooccurrencesPerTimeId) {
        Map<Cooccurrence, Cooccurrence> cooccurrencesInTimeId = allCooccurrencesPerTimeId.get(timeId);

        final SmallestValue smallest = new SmallestValue();
        clusterCooccurrences.forEach(c -> {
            Cooccurrence cooccurrence = cooccurrencesInTimeId.get(new Cooccurrence(c.getWord1(), c.getWord2(), timeId));
            if (cooccurrence == null) {
                smallest.setValue(0);
            } else {
                long frequency = cooccurrence.getFrequency(timeId.getDocFreq());
                smallest.setValue(Math.min(smallest.value, frequency));
            }
        });

        return smallest.getValue();
    }

    @Data
    static class SmallestValue {
        private long value = Long.MAX_VALUE;
    }
}

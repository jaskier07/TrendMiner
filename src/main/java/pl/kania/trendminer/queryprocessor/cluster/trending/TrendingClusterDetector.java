package pl.kania.trendminer.queryprocessor.cluster.trending;

import org.springframework.stereotype.Service;
import pl.kania.trendminer.db.model.Cooccurrence;
import pl.kania.trendminer.db.model.TimeId;
import pl.kania.trendminer.queryprocessor.SmallestFrequencyFinder;
import pl.kania.trendminer.queryprocessor.cluster.model.Cluster;
import pl.kania.trendminer.queryprocessor.cluster.model.CooccurrenceAllPeriods;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrendingClusterDetector {

    public List<TrendingClusterResult> detect(TrendingClusterParameters params) {
        List<TrendingClusterResult> results = new ArrayList<>();
        List<TimeId> orderedTimeIdsAsc = getOrderedTimeIdsByStartTimeAscending(params);

        params.getWordClusters().forEach(cluster -> {
            AverageFrequency averageFrequency = getAverageFrequency(params, orderedTimeIdsAsc, cluster);
            double burstiness = new BurstinessCounter().count(averageFrequency);
            if (burstiness > params.getThresholdBurstiness()) {
                results.add(new TrendingClusterResult(cluster, burstiness));
            }
        });

        return results.stream()
                .sorted(Comparator.comparing(TrendingClusterResult::getBurstiness))
                .collect(Collectors.toList());
    }

    /**
     * Generates all possible word cooccurrences and in each period finds the smallest frequency value.
     */
    private AverageFrequency getAverageFrequency(TrendingClusterParameters params, List<TimeId> orderedTimeIds, Cluster cluster) {
        AverageFrequency averageFrequency = new AverageFrequency(params.getPeriodsBeforeUserStart());
        orderedTimeIds.forEach(timeId -> {
            Set<CooccurrenceAllPeriods> allPossibleCooccurrences = cluster.getAllPossibleCooccurrences();
            allPossibleCooccurrences.forEach(c -> {
                Cooccurrence cooccurrence = params.getAllCooccurrencesPerTimeId().get(timeId).get(new Cooccurrence(c.getWord1(), c.getWord2(), timeId));
                c.setSupport(cooccurrence == null ? 0. : cooccurrence.getSupport());
            });

            long estimatedFrequency = new SmallestFrequencyFinder().findInTimeId(allPossibleCooccurrences, timeId, params.getAllCooccurrencesPerTimeId().get(timeId));
            averageFrequency.addFrequency(estimatedFrequency);
        });
        return averageFrequency;
    }

    private List<TimeId> getOrderedTimeIdsByStartTimeAscending(TrendingClusterParameters params) {
        return params.getAllCooccurrencesPerTimeId().keySet().stream()
                .sorted(Comparator.comparing(TimeId::getStartTime))
                .collect(Collectors.toList());
    }
}

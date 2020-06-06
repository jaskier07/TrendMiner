package pl.kania.trendminer.queryprocessor.cluster.generation;

import lombok.extern.slf4j.Slf4j;
import pl.kania.trendminer.queryprocessor.cluster.model.CooccurrenceAllPeriods;
import pl.kania.trendminer.queryprocessor.SupportComputer;
import pl.kania.trendminer.queryprocessor.cluster.model.Cluster;
import pl.kania.trendminer.queryprocessor.cluster.model.ClusterSize;
import pl.kania.trendminer.util.ProgressLogger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ClusterGenerator {

    private final Map<CooccurrenceAllPeriods, CooccurrenceAllPeriods> cooccurrences;
    private final SupportComputer supportComputer;

    public ClusterGenerator(Set<CooccurrenceAllPeriods> cooccurrences, double thresholdSupport) {
        this.supportComputer = new SupportComputer(thresholdSupport);
        this.cooccurrences = cooccurrences
                .stream()
                .collect(Collectors.toMap(c -> c, c -> c));
    }

    public Map<ClusterSize, List<Cluster>> generateLargerWordClusters(List<Cluster> twoWordClusters) {
        Map<ClusterSize, List<Cluster>> wordClustersPerSize = new HashMap<>();
        wordClustersPerSize.put(ClusterSize.TWO, twoWordClusters);

        long counter = 0;
        log.info("Generating larger world clusters started.");
        for (ClusterSize clusterSize = ClusterSize.TWO; !wordClustersPerSize.get(clusterSize).isEmpty(); clusterSize = ClusterSize.next(clusterSize)) {
            Set<Cluster> nextWordClusters = new HashSet<>();
            ClusterSize nextClusterSize = ClusterSize.next(clusterSize);

            for (int j = 0; j < wordClustersPerSize.get(clusterSize).size(); j++) {
                // FIXME start from k = j + 1?
                for (int k = 0; k < wordClustersPerSize.get(clusterSize).size(); k++) {
                    Cluster cluster1 = wordClustersPerSize.get(clusterSize).get(j);
                    Cluster cluster2 = wordClustersPerSize.get(clusterSize).get(k);
                    if (!cluster1.equals(cluster2)) {
                        generate(cluster1, cluster2).ifPresent(nextWordClusters::add);
                    }
                }
                ProgressLogger.log(counter++, 20000);
            }
            ProgressLogger.done();
            log.info("Generating " + ClusterSize.getSize(nextClusterSize) + "-clusters ended. Generated " + nextWordClusters.size() + " clusters.");

            wordClustersPerSize.put(nextClusterSize, List.of(nextWordClusters.toArray(new Cluster[0])));
        }
        log.info("Generating larger world clusters ended.");

        return wordClustersPerSize;
    }

    private Optional<Cluster> generate(Cluster cluster1, Cluster cluster2) {
        requireSameSize(cluster1, cluster2);
        ClusterSize clusterSize = cluster1.getSize();

        boolean clustersHaveCommonWords = clustersHaveCommonWords(cluster1, cluster2, clusterSize);
        String lastWordCluster1 = cluster1.getLastWord();
        String lastWordCluster2 = cluster2.getLastWord();

        if (clustersHaveCommonWords && supportComputer.meetsThreshold(getSupport(lastWordCluster1, lastWordCluster2))) {
            if (lastWordCluster1.compareTo(lastWordCluster2) < 0) {
                return Optional.of(Cluster.ofBiggerSize(cluster1, lastWordCluster2));
            } else {
                return Optional.of(Cluster.ofBiggerSize(cluster2, lastWordCluster1));
            }
        }

        return Optional.empty();
    }

    private double getSupport(String word1, String word2) {
        CooccurrenceAllPeriods cooccurrence = cooccurrences.get(new CooccurrenceAllPeriods(word1, word2));
        if (cooccurrence != null) {
            return cooccurrence.getSupport();
        }
        return 0;
    }

    private boolean clustersHaveCommonWords(Cluster cluster1, Cluster cluster2, ClusterSize clusterSize) {
        // FIXME -1 - 1
        for (int n = 0; n < ClusterSize.getSize(clusterSize) - 1; n++) {
//            for (int j = 0; j <= ClusterSize.getSize(clusterSize); j++) {
                String wordCluster1 = cluster1.getWords().get(n);
                String wordCluster2 = cluster2.getWords().get(n);

                if (!wordCluster1.equals(wordCluster2)) {
                    return false;
                }
//            }
        }
        return true;
    }

    private void requireSameSize(Cluster cluster1, Cluster cluster2) {
        if (!Objects.equals(cluster1.getSize(), cluster2.getSize())) {
            throw new IllegalStateException("Clusters have to have same size!");
        }
    }
}

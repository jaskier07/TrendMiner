package pl.kania.trendminer.queryprocessor.result;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.kania.trendminer.queryprocessor.cluster.model.Cluster;
import pl.kania.trendminer.queryprocessor.cluster.model.ClusterSize;
import pl.kania.trendminer.queryprocessor.cluster.model.ClusterSizeComparator;
import pl.kania.trendminer.queryprocessor.cluster.trending.TrendingClusterResult;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResultPrinter {

    public static void sortAndPrintResults(List<TrendingClusterResult> results) {
        List<Cluster> clusters = results.stream()
                .peek(c -> c.getCluster().setBurstiness(c.getBurstiness()))
                .map(TrendingClusterResult::getCluster)
                .collect(Collectors.toList());
        printResults(clusters);
    }

    public static void printResults(Map<ClusterSize, List<Cluster>> wordClustersPerSize) {
        int minClusterSize = 3;
        if (wordClustersPerSize.size() < minClusterSize) {
            log.info(wordClustersPerSize.values().stream()
                    .findFirst()
                    .get()
                    .toString());
        } else {
            List<List<Cluster>> clusters = wordClustersPerSize.entrySet()
                    .stream()
                    .sorted(Comparator.comparingInt(o -> o.getKey().ordinal()))
                    .map(Map.Entry::getValue)
                    .skip(minClusterSize - 2)
                    .collect(Collectors.toList());

            int size = minClusterSize;
            for (List<Cluster> cluster : clusters) {
                if (!cluster.isEmpty()) {
                    log.info("============= CLUSTERS OF SIZE " + size++ + " ===============> ");
                    cluster.forEach(c -> log.info(c.toString()));
                }
            }
        }
    }

    public static void printResults(List<Cluster> trendingClusters) {
        new SubsetsRemoval().removeSubsets(trendingClusters);
        trendingClusters.sort(Comparator.comparing(Cluster::getBurstiness).reversed());

        log.info("Trending clusters:");
        trendingClusters.forEach(t -> {
            if (t.getSize().ordinal() > 0) {
                log.info(t.toString() + ", " + t.getBurstiness());
            }
        });
    }
}

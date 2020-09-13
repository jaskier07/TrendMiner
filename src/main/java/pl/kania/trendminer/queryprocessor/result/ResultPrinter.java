package pl.kania.trendminer.queryprocessor.result;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.kania.trendminer.dataparser.parser.ImproveResults;
import pl.kania.trendminer.queryprocessor.cluster.model.Cluster;
import pl.kania.trendminer.queryprocessor.cluster.model.ClusterSize;
import pl.kania.trendminer.queryprocessor.cluster.model.ClusterSizeComparator;
import pl.kania.trendminer.queryprocessor.cluster.trending.TrendingClusterResult;
import pl.kania.trendminer.util.Counter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResultPrinter {

    public static void sortAndPrintResults(List<TrendingClusterResult> results, boolean improveResults) {
        List<Cluster> clusters = setBurstinessAndMapToCluster(results);
        printResults(clusters, improveResults);
    }

    private static List<Cluster> setBurstinessAndMapToCluster(List<TrendingClusterResult> results) {
        return results.stream()
                .peek(c -> c.getCluster().setBurstiness(c.getBurstiness()))
                .map(TrendingClusterResult::getCluster)
                .collect(Collectors.toList());
    }

    public static List<Cluster> getSortedResults(List<TrendingClusterResult> results, int clustersSize) {
        List<Cluster> clusters = setBurstinessAndMapToCluster(results);
        return sortResults(clusters, false, clustersSize);
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

    public static void printResults(List<Cluster> trendingClusters, boolean improveResults) {
        trendingClusters = sortResults(trendingClusters, improveResults, 30);

        log.info("\n\nTrending clusters:");
        Counter ctr = new Counter();
        trendingClusters.forEach(t -> {
            log.info("#" + ctr.getValueAsStringAndIncrement() + ": " + t.getBurstiness() + " : " + t.toString() + "");
        });
    }

    private static List<Cluster> sortResults(List<Cluster> trendingClusters, boolean improveResults, int resultLimit) {
        if (improveResults) {
            new SubsetsRemoval().removeSubsets(trendingClusters);
        }
        trendingClusters.sort(Comparator.comparing(Cluster::getBurstiness).reversed());
        trendingClusters = trendingClusters.stream()
                .filter(c -> c.getSize().ordinal() > ClusterSize.TWO.ordinal())
                .limit(resultLimit)
                .collect(Collectors.toList());
        return trendingClusters;
    }
}

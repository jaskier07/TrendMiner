package pl.kania.trendminer.queryprocessor.result;

import pl.kania.trendminer.queryprocessor.cluster.model.Cluster;
import pl.kania.trendminer.queryprocessor.cluster.model.ClusterSizeComparator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SubsetsRemoval {

    void removeSubsets(List<Cluster> clusters) {
        List<Cluster> sortedClusters = sortClustersBySize(clusters);
        Set<Cluster> clustersToRemove = new HashSet<>();

        for (int i = 0; i < sortedClusters.size(); i++) {
            Cluster currentCluster = sortedClusters.get(i);
            for (int j = 0; j < i; j++) {
                Cluster previousCluster = sortedClusters.get(j);
                if (currentCluster.getWords().containsAll(previousCluster.getWords())) {
                    clustersToRemove.add(previousCluster);
                }
            }
        }

        clusters.removeAll(clustersToRemove);
    }

    private List<Cluster> sortClustersBySize(List<Cluster> clusters) {
        return clusters.stream()
                .sorted(new ClusterSizeComparator())
                .distinct()
                .collect(Collectors.toList());
    }
}

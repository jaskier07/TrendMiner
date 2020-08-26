package pl.kania.trendminer.queryprocessor.cluster.trending;


import lombok.Value;
import pl.kania.trendminer.queryprocessor.cluster.model.Cluster;

@Value
public class TrendingClusterResult {
    Cluster cluster;
    double burstiness;
}

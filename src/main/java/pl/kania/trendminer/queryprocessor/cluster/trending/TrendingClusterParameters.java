package pl.kania.trendminer.queryprocessor.cluster.trending;


import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import pl.kania.trendminer.db.model.Cooccurrence;
import pl.kania.trendminer.db.model.TimeId;
import pl.kania.trendminer.queryprocessor.cluster.model.Cluster;

import java.util.List;
import java.util.Map;

@Value
@Builder
public class TrendingClusterParameters {
    @NonNull List<Cluster> wordClusters;
    @NonNull Map<TimeId, Map<Cooccurrence, Cooccurrence>> allCooccurrencesPerTimeId;
    @NonNull int periodsBeforeUserStart;
    @NonNull double thresholdBurstiness;
}

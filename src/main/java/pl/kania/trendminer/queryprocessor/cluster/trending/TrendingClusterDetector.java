package pl.kania.trendminer.queryprocessor.cluster.trending;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.kania.trendminer.dao.CooccurrenceDao;
import pl.kania.trendminer.dataparser.parser.WordCooccurrence;
import pl.kania.trendminer.model.Cooccurrence;
import pl.kania.trendminer.model.TimeId;
import pl.kania.trendminer.queryprocessor.cluster.model.Cluster;
import pl.kania.trendminer.queryprocessor.cluster.model.ClusterSize;
import pl.kania.trendminer.util.ProgressLogger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TrendingClusterDetector {

    private final double burstinessThreshold;

    public TrendingClusterDetector(@Autowired Environment environment) {
        burstinessThreshold = Double.parseDouble(environment.getProperty("pl.kania.time.threshold-burstiness"));
    }

    public List<Cluster> detect(Map<ClusterSize, List<Cluster>> wordClustersPerSize, Map<TimeId, List<Cooccurrence>> allCooccurrencesPerId) {
        Map<TimeId, Map<WordCooccurrence, Double>> supportValuesPerTimeId = getSupportValuesPerTimeId(allCooccurrencesPerId);
        List<Cluster> clustersMeetingThreshold = new ArrayList<>();

        int counter = 0;
        long notFoundCounter = 0;
        long totalCount = 0;
        for (List<Cluster> wordClusters : wordClustersPerSize.values()) {
            for (Cluster cluster : wordClusters) {
                BurstinessCounter.Result result = BurstinessCounter.getBurstiness(cluster, supportValuesPerTimeId);
                double burstiness = result.getBurstiness();
                notFoundCounter += result.getNotFoundCount();
                totalCount += result.getTotalCount();
                if (burstiness >= burstinessThreshold && burstiness != Double.MAX_VALUE) {
                    cluster.setBurstiness(burstiness);
                    clustersMeetingThreshold.add(cluster);
                }
                ProgressLogger.log(counter++);
            }
        }
        ProgressLogger.done();
//        log.warn("Percentage of not found: " + 100.*notFoundCounter/totalCount);

        clustersMeetingThreshold.sort(Comparator.comparing(Cluster::getBurstiness).reversed());
        return clustersMeetingThreshold;
    }

    private Map<TimeId, Map<WordCooccurrence, Double>> getSupportValuesPerTimeId(Map<TimeId, List<Cooccurrence>> allCooccurrencesPerId) {
        Map<TimeId, Map<WordCooccurrence, Double>> supportValues = new HashMap<>();
        for (Map.Entry<TimeId, List<Cooccurrence>> cooccurrencesPerId : allCooccurrencesPerId.entrySet()) {
            Map<WordCooccurrence, Double> supportPerCooccurrence = new HashMap<>();
            for (Cooccurrence cooccurrence : cooccurrencesPerId.getValue()) {
                WordCooccurrence wordCooccurrence = new WordCooccurrence(cooccurrence.getWord1().getWord(), cooccurrence.getWord2().getWord(), cooccurrence.getSupport());
                supportPerCooccurrence.put(wordCooccurrence, cooccurrence.getSupport());
            }
            supportValues.put(cooccurrencesPerId.getKey(), supportPerCooccurrence);
        }
        return supportValues;
    }
}

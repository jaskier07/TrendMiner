package pl.kania.trendminer.queryprocessor.cluster;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.kania.trendminer.util.ProgressLogger;
import pl.kania.trendminer.dao.CooccurrenceDao;
import pl.kania.trendminer.model.Cooccurrence;
import pl.kania.trendminer.model.TimeId;
import pl.kania.trendminer.queryprocessor.cluster.model.CooccurrenceAllPeriods;
import pl.kania.trendminer.queryprocessor.SupportComputer;
import pl.kania.trendminer.queryprocessor.cluster.generation.ClusterGenerator;
import pl.kania.trendminer.queryprocessor.cluster.generation.TwoWordClusterGenerator;
import pl.kania.trendminer.queryprocessor.cluster.model.Cluster;
import pl.kania.trendminer.queryprocessor.cluster.model.ClusterSize;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class FrequentlyOccurringClusterIdentifier {

    private final CooccurrenceDao cooccurrenceDao;
    private final Environment environment;
    private final SupportComputer supportComputer;
    private final TwoWordClusterGenerator twoWordClusterGenerator;

    public FrequentlyOccurringClusterIdentifier(@Autowired CooccurrenceDao cooccurrenceDao, @Autowired Environment environment,
                                                @Autowired TwoWordClusterGenerator twoWordClusterGenerator) {
        this.twoWordClusterGenerator = twoWordClusterGenerator;
        this.cooccurrenceDao = cooccurrenceDao;
        this.environment = environment;
        this.supportComputer = new SupportComputer(Double.parseDouble(environment.getProperty("pl.kania.time.threshold-support")));
    }

    public void identify(List<TimeId> timeIds) {
        Map<TimeId, List<Cooccurrence>> allCooccurrencesPerId = new HashMap<>();
        timeIds.forEach(t -> allCooccurrencesPerId.put(t, cooccurrenceDao.findAllByTimeIDId(t.getId())));

        Long totalFrequency = getTotalFrequency(timeIds);

        TwoWordClusterGenerator.Result clusterGeneratorResult = twoWordClusterGenerator.createTwoWordClusters(allCooccurrencesPerId, totalFrequency);
        List<Cluster> twoWordClusters = clusterGeneratorResult.getClusters();
        Map<ClusterSize, List<Cluster>> wordClusters = generateLargerWordClusters(twoWordClusters, clusterGeneratorResult.getCooccurrences());

        // TODO condition on burstiness threshold
    }

    private Long getTotalFrequency(List<TimeId> timeIds) {
        return timeIds.stream()
                .map(TimeId::getDocFreq)
                .reduce(Long::sum)
                .orElseThrow();
    }

    private Map<ClusterSize, List<Cluster>> generateLargerWordClusters(List<Cluster> twoWordClusters, Set<CooccurrenceAllPeriods> cooccurrences) {
        Map<ClusterSize, List<Cluster>> wordClustersPerSize = new HashMap<>();
        wordClustersPerSize.put(ClusterSize.TWO, twoWordClusters);
        ClusterGenerator clusterGeneration = new ClusterGenerator(cooccurrences, Double.parseDouble(environment.getProperty("pl.kania.time.threshold-support")));

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
                        clusterGeneration.generate(cluster1, cluster2).ifPresent(nextWordClusters::add);
                    }
                }
                ProgressLogger.log(counter++, 20000);
            }
            ProgressLogger.done();
            log.info("Generating " + ClusterSize.getSize(nextClusterSize) + "-clusters ended. Generated " + nextWordClusters.size() + " clusters.");

            wordClustersPerSize.put(nextClusterSize, List.of(nextWordClusters.toArray(new Cluster[0])));
        }
        log.info("Generating larger world clusters ended.");
        printResults(wordClustersPerSize);

        return wordClustersPerSize;
    }

    private void printResults(Map<ClusterSize, List<Cluster>> wordClustersPerSize) {
        if (wordClustersPerSize.size() < 3) {
            log.info(wordClustersPerSize.values().stream()
                    .findFirst()
                    .get()
                    .toString());
        } else {
            List<Cluster> clusters = wordClustersPerSize.values()
                    .stream()
                    .skip(wordClustersPerSize.size() - 3)
                    .findFirst()
                    .get();
            clusters.forEach(c -> log.info(c.toString()));
        }
    }
}

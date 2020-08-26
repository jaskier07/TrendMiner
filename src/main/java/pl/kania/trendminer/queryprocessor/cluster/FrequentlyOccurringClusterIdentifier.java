package pl.kania.trendminer.queryprocessor.cluster;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.kania.trendminer.queryprocessor.TimeIdProvider;
import pl.kania.trendminer.queryprocessor.cluster.trending.TrendingClusterParameters;
import pl.kania.trendminer.queryprocessor.cluster.trending.TrendingClusterResult;
import pl.kania.trendminer.queryprocessor.result.ResultPrinter;
import pl.kania.trendminer.queryprocessor.cluster.trending.TrendingClusterDetector;
import pl.kania.trendminer.dao.CooccurrenceDao;
import pl.kania.trendminer.model.Cooccurrence;
import pl.kania.trendminer.model.TimeId;
import pl.kania.trendminer.queryprocessor.cluster.generation.ClusterGenerator;
import pl.kania.trendminer.queryprocessor.cluster.generation.TwoWordClusterGenerator;
import pl.kania.trendminer.queryprocessor.cluster.model.Cluster;
import pl.kania.trendminer.queryprocessor.cluster.model.ClusterSize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FrequentlyOccurringClusterIdentifier {

    private final CooccurrenceDao cooccurrenceDao;
    private final TwoWordClusterGenerator twoWordClusterGenerator;
    private final TrendingClusterDetector trendingClusterDetector;
    private final TimeIdProvider timeIdProvider;
    private final double thresholdSupport;
    private final double thresholdBurstiness;

    public FrequentlyOccurringClusterIdentifier(@Autowired CooccurrenceDao cooccurrenceDao,
                                                @Autowired Environment environment,
                                                @Autowired TwoWordClusterGenerator twoWordClusterGenerator,
                                                @Autowired TrendingClusterDetector trendingClusterDetector,
                                                @Autowired TimeIdProvider timeIdProvider) {
        this.twoWordClusterGenerator = twoWordClusterGenerator;
        this.cooccurrenceDao = cooccurrenceDao;
        this.trendingClusterDetector = trendingClusterDetector;
        this.timeIdProvider = timeIdProvider;
        thresholdSupport = Double.parseDouble(environment.getProperty("pl.kania.time.threshold-support"));
        thresholdBurstiness = Double.parseDouble(environment.getProperty("pl.kania.time.threshold-burstiness"));
    }

    public void identify() {
        Map<TimeId, List<Cooccurrence>> occurrencesInRangePerId = getCooccurrencesPerTimeId(timeIdProvider.getTimeIdsInRange());

        TwoWordClusterGenerator.Result clusterGeneratorResult = twoWordClusterGenerator.createTwoWordClusters(occurrencesInRangePerId, timeIdProvider.getTimeIdsInRange());
        List<Cluster> twoWordClusters = clusterGeneratorResult.getClusters();

        Map<ClusterSize, List<Cluster>> wordClusters = new ClusterGenerator(clusterGeneratorResult.getCooccurrences(), thresholdSupport)
                .generateLargerWordClusters(twoWordClusters);
        ResultPrinter.printResults(wordClusters);

        TrendingClusterParameters params = TrendingClusterParameters.builder()
                .allCooccurrencesPerTimeId(getAllCooccurrencesPerId())
                .thresholdBurstiness(thresholdBurstiness)
                .wordClusters(mergeClustersFromMap(wordClusters))
                .periodsBeforeUserStart(timeIdProvider.getPeriodsBeforeUserStart())
                .build();
        List<TrendingClusterResult> results = trendingClusterDetector.detect(params);
        ResultPrinter.sortAndPrintResults(results);
    }

    private Map<TimeId, List<Cooccurrence>> getCooccurrencesPerTimeId(List<TimeId> timeIds) {
        Map<TimeId, List<Cooccurrence>> occurrencesInRangePerId = new HashMap<>();
        timeIds.forEach(t -> occurrencesInRangePerId.put(t, getAllCooccurrencesByTimeId(t)));
        return occurrencesInRangePerId;
    }

    private List<Cluster> mergeClustersFromMap(Map<ClusterSize, List<Cluster>> wordClusters) {
        List<Cluster> clusters = new ArrayList<>();
        wordClusters.values().forEach(clusters::addAll);
        return clusters;
    }

    private Map<TimeId, Map<Cooccurrence, Cooccurrence>> getAllCooccurrencesPerId() {
        Map<TimeId, Map<Cooccurrence, Cooccurrence>> cooccurrencesPerTimedId = new HashMap<>();

        Map<TimeId, List<Cooccurrence>> allCooccurrencesPerTimeId = getCooccurrencesPerTimeId(timeIdProvider.getAllTimeIds());
        allCooccurrencesPerTimeId.forEach((timeId, cooccurrences) -> {
            Map<Cooccurrence, Cooccurrence> map = cooccurrences.stream()
                    .collect(Collectors.toMap(Function.identity(), Function.identity()));
            cooccurrencesPerTimedId.put(timeId, map);
        });

        return cooccurrencesPerTimedId;
    }

    private List<Cooccurrence> getAllCooccurrencesByTimeId(TimeId t) {
        return cooccurrenceDao.findAllByTimeIDId(t.getId());
    }
}

package pl.kania.trendminer.queryprocessor.cluster;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.kania.trendminer.queryprocessor.ResultPrinter;
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

import javax.swing.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FrequentlyOccurringClusterIdentifier {

    private final CooccurrenceDao cooccurrenceDao;
    private final TwoWordClusterGenerator twoWordClusterGenerator;
    private final double thresholdSupport;

    public FrequentlyOccurringClusterIdentifier(@Autowired CooccurrenceDao cooccurrenceDao, @Autowired Environment environment,
                                                @Autowired TwoWordClusterGenerator twoWordClusterGenerator) {
        this.twoWordClusterGenerator = twoWordClusterGenerator;
        this.cooccurrenceDao = cooccurrenceDao;
        thresholdSupport = Double.parseDouble(environment.getProperty("pl.kania.time.threshold-support"));
    }

    public void identify(List<TimeId> timeIds) {
        Map<TimeId, List<Cooccurrence>> allCooccurrencesPerId = new HashMap<>();
        timeIds.forEach(t -> allCooccurrencesPerId.put(t, cooccurrenceDao.findAllByTimeIDId(t.getId())));

        Long totalFrequency = getTotalFrequency(timeIds);

        TwoWordClusterGenerator.Result clusterGeneratorResult = twoWordClusterGenerator.createTwoWordClusters(allCooccurrencesPerId, totalFrequency);
        List<Cluster> twoWordClusters = clusterGeneratorResult.getClusters();

        Map<ClusterSize, List<Cluster>> wordClusters = new ClusterGenerator(clusterGeneratorResult.getCooccurrences(), thresholdSupport)
                .generateLargerWordClusters(twoWordClusters);
        ResultPrinter.printResults(wordClusters);
    }

    private Long getTotalFrequency(List<TimeId> timeIds) {
        return timeIds.stream()
                .map(TimeId::getDocFreq)
                .reduce(Long::sum)
                .orElseThrow();
    }
}

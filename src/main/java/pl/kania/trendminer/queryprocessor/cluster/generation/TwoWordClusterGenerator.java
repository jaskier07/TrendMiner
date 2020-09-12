package pl.kania.trendminer.queryprocessor.cluster.generation;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.kania.trendminer.db.model.Cooccurrence;
import pl.kania.trendminer.db.model.TimeId;
import pl.kania.trendminer.queryprocessor.cluster.model.CooccurrenceAllPeriods;
import pl.kania.trendminer.queryprocessor.SupportComputer;
import pl.kania.trendminer.queryprocessor.cluster.model.Cluster;
import pl.kania.trendminer.queryprocessor.cluster.model.ClusterSize;

import java.util.*;

@Slf4j
@Service
public class TwoWordClusterGenerator {

    private final Environment environment;
    private final SupportComputer supportComputer;

    public TwoWordClusterGenerator(@Autowired Environment environment) {
        this.environment = environment;
        this.supportComputer = new SupportComputer(Double.parseDouble(environment.getProperty("pl.kania.time.threshold-support")));
    }

    public Result createTwoWordClusters(Map<TimeId, List<Cooccurrence>> cooccurrencesPerTimeId, List<TimeId> timeIds) {
        log.info("Starting creating two word clusters...");
        Long totalFrequency = getTotalFrequency(timeIds);
        Map<CooccurrenceAllPeriods, Double> supportPerCooccurrenceInAllPeriods = sumSupportForWordCooccurrenceInAllPeriods(cooccurrencesPerTimeId, timeIds);
        setSupportForWordCooccurrences(totalFrequency, supportPerCooccurrenceInAllPeriods);

        Set<CooccurrenceAllPeriods> allCooccurrencesWithSupport = supportPerCooccurrenceInAllPeriods.keySet();
        Set<CooccurrenceAllPeriods> cooccurrencesMeetingThreshold = new HashSet<>();
        allCooccurrencesWithSupport.stream()
                .filter(c -> supportComputer.meetsThreshold(c.getSupport()))
                .forEach(cooccurrencesMeetingThreshold::add);

        List<Cluster> twoWordClusters = new ArrayList<>();
        cooccurrencesMeetingThreshold.forEach(c -> twoWordClusters.add(new Cluster(ClusterSize.TWO, c.getWords())));
        log.info("Creating two word clusters ended.");
        return new Result(twoWordClusters, allCooccurrencesWithSupport);
    }

    private void setSupportForWordCooccurrences(Long totalFrequency, Map<CooccurrenceAllPeriods, Double> supportPerCooccurrenceInAllPeriods) {
        supportPerCooccurrenceInAllPeriods.forEach((cooccurrence, supportSum) -> cooccurrence.setSupport(supportSum / totalFrequency));
    }

    private Map<CooccurrenceAllPeriods, Double> sumSupportForWordCooccurrenceInAllPeriods(Map<TimeId, List<Cooccurrence>> cooccurrencesPerTimeId, List<TimeId> timeIds) {
        Map<CooccurrenceAllPeriods, Double> supportPerCooccurrenceInAllPeriods = new HashMap<>();
        Map<Long, Long> docsProcessedPerTimeId = getDocumentsProcessed(timeIds);
        cooccurrencesPerTimeId.values().forEach(list -> list.forEach(cooccurrence -> {
            Long documentsProcessed = docsProcessedPerTimeId.get(cooccurrence.getTimeID().getId());
            supportPerCooccurrenceInAllPeriods.merge(new CooccurrenceAllPeriods(cooccurrence), cooccurrence.getSupport() * documentsProcessed, Double::sum);
        }));
        return supportPerCooccurrenceInAllPeriods;
    }

    private Map<Long, Long> getDocumentsProcessed(List<TimeId> timeIds) {
        Map<Long, Long> map = new HashMap<>();
        timeIds.forEach(t -> map.put(t.getId(), t.getDocFreq()));
        return map;
    }

    private Long getTotalFrequency(List<TimeId> timeIds) {
        return timeIds.stream()
                .map(TimeId::getDocFreq)
                .reduce(Long::sum)
                .orElseThrow();
    }

    @Value
    public static class Result {
        List<Cluster> clusters;
        Set<CooccurrenceAllPeriods> cooccurrences;
    }
}

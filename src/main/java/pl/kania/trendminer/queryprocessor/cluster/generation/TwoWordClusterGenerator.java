package pl.kania.trendminer.queryprocessor.cluster.generation;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.kania.trendminer.model.Cooccurrence;
import pl.kania.trendminer.model.TimeId;
import pl.kania.trendminer.queryprocessor.cluster.model.CooccurrenceAllPeriods;
import pl.kania.trendminer.queryprocessor.SupportComputer;
import pl.kania.trendminer.queryprocessor.cluster.model.Cluster;
import pl.kania.trendminer.queryprocessor.cluster.model.ClusterSize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class TwoWordClusterGenerator {

    private final Environment environment;
    private final SupportComputer supportComputer;

    public TwoWordClusterGenerator(@Autowired Environment environment) {
        this.environment = environment;
        this.supportComputer = new SupportComputer(Double.parseDouble(environment.getProperty("pl.kania.time.threshold-support")));
    }

    public Result createTwoWordClusters(Map<TimeId, List<Cooccurrence>> cooccurrencesPerTimeId, Long totalFrequency) {
        log.info("Starting creating two word clusters...");
        Map<CooccurrenceAllPeriods, Double> supportPerCooccurrenceInAllPeriods = sumSupportForWordCooccurrenceInAllPeriods(cooccurrencesPerTimeId);
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

    private Map<CooccurrenceAllPeriods, Double> sumSupportForWordCooccurrenceInAllPeriods(Map<TimeId, List<Cooccurrence>> cooccurrencesPerTimeId) {
        Map<CooccurrenceAllPeriods, Double> supportPerCooccurrenceInAllPeriods = new HashMap<>();
        cooccurrencesPerTimeId.values().forEach(list -> list.forEach(cooccurrence ->
                supportPerCooccurrenceInAllPeriods.merge(new CooccurrenceAllPeriods(cooccurrence), cooccurrence.getSupport(), Double::sum)));
        return supportPerCooccurrenceInAllPeriods;
    }
    
    @Value
    public static class Result {
         List<Cluster> clusters;
         Set<CooccurrenceAllPeriods> cooccurrences;
    } 
}

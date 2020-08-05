package pl.kania.trendminer.queryprocessor.cluster.model;

import java.util.*;
import java.util.stream.Collectors;

public class ClusterSizeComparator implements Comparator<Cluster> {
    @Override
    public int compare(Cluster o1, Cluster o2) {
        int comparisonResult = o1.getSize().compareTo(o2.getSize());
        return comparisonResult;
    }
}

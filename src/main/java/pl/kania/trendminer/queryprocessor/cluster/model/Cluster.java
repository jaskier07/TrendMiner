package pl.kania.trendminer.queryprocessor.cluster.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class Cluster {
    private final ClusterSize size;
    private final List<String> words;

    public Cluster(ClusterSize size, List<String> words) {
        this.size = size;
        this.words = Collections.unmodifiableList(words);
    }

    public String getLastWord() {
        return words.get(words.size() - 1);
    }

    @Override
    public String toString() {
        return words.toString();
    }

    public static Cluster ofBiggerSize(Cluster cluster, String newWord) {
        List<String> words = cluster.getWords();
        List<String> newWords = new ArrayList<>(words);
        newWords.add(newWord);
        return new Cluster(ClusterSize.next(cluster.getSize()), newWords);
    }
}

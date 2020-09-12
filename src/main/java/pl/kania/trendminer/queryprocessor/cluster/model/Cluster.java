package pl.kania.trendminer.queryprocessor.cluster.model;

import lombok.Data;
import pl.kania.trendminer.db.model.Word;

import java.util.*;

@Data
public class Cluster {
    private final ClusterSize size;
    private final List<Word> words;
    private double burstiness;

    public Cluster(ClusterSize size, List<Word> words) {
        this.size = size;
        this.words = Collections.unmodifiableList(words);
    }

    public Word getLastWord() {
        return words.get(words.size() - 1);
    }

    @Override
    public String toString() {
        return words.toString();
    }

    public static Cluster ofBiggerSize(Cluster cluster, Word newWord) {
        List<Word> words = cluster.getWords();
        List<Word> newWords = new ArrayList<>(words);
        newWords.add(newWord);
        return new Cluster(ClusterSize.next(cluster.getSize()), newWords);
    }

    public int getIntSize() {
        return ClusterSize.getSize(size);
    }

    public Set<CooccurrenceAllPeriods> getAllPossibleCooccurrences() {
        Set<CooccurrenceAllPeriods> cooccurrences = new HashSet<>();
        for (int i = 0; i < words.size(); i++) {
            for (int j = i + 1; j < words.size(); j++) {
                cooccurrences.add(new CooccurrenceAllPeriods(words.get(i), words.get(j)));
            }
        }
        return cooccurrences;
    }
}

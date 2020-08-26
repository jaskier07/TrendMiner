package pl.kania.trendminer.queryprocessor.cluster.model;

import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.kania.trendminer.model.Word;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ClusterTest {

    @Test
    void givenClusterWithWordsGetAllPossibleCooccurrences() {
        Word word1 = new Word("yes");
        word1.setId(1L);

        Word word2 = new Word("we");
        word2.setId(2L);

        Word word3 = new Word("can");
        word3.setId(3L);

        List<Word> words = Arrays.asList(word1, word2, word3);
        Cluster cluster = new Cluster(ClusterSize.THREE, words);

        Assertions.assertEquals(getAllPossibleCooccurrences(word1, word2, word3), cluster.getAllPossibleCooccurrences());
    }

    private Set<CooccurrenceAllPeriods> getAllPossibleCooccurrences(Word word1, Word word2, Word word3) {
        return Set.of(new CooccurrenceAllPeriods(word1, word2), new CooccurrenceAllPeriods(word1, word3), new CooccurrenceAllPeriods(word2, word3));
    }
}
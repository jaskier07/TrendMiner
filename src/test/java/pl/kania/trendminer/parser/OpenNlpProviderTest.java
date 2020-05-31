package pl.kania.trendminer.parser;

import lombok.Value;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class OpenNlpProviderTest {

    private OpenNlpProvider openNlpProvider;

    public OpenNlpProviderTest() {
        this.openNlpProvider = new OpenNlpProvider();
    }

    @ParameterizedTest
    @MethodSource("sentenceProvider")
    void havingSetOfSentencesCheckIfAreProperlySeparated(TestSentences testSentences) {
        String[] expected = testSentences.getDividedSentences();
        String[] actual = openNlpProvider.divideIntoSentences(testSentences.getSetOfSentences());
        Assertions.assertArrayEquals(expected, actual);
    }

    static Stream<TestSentences> sentenceProvider() {
        TestSentences sentence1 = new TestSentences("I have a great cat. How is it? It is good, but always hungry.",
                new String[]{"I have a great cat.", "How is it?", "It is good, but always hungry."});
        TestSentences sentence2 = new TestSentences("It can't be! What a news. It is unbelievable.",
                new String[]{"It can't be!", "What a news.", "It is unbelievable."});
        return Stream.of(sentence1, sentence2);
    }

    @Value
    private static class TestSentences {
         String setOfSentences;
         String[] dividedSentences;
    }
}
package pl.kania.trendminer.dataparser.parser;

import lombok.Value;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import pl.kania.trendminer.QueryProcessorApplication;

import java.util.stream.Stream;

@SpringBootTest(classes = QueryProcessorApplication.class)
class OpenNlpProviderTest {

    private final OpenNlpProvider openNlpProvider;

    public OpenNlpProviderTest(@Autowired OpenNlpProvider openNlpProvider) {
        this.openNlpProvider = openNlpProvider;
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
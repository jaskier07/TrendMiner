package pl.kania.trendminer.dataparser.parser;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WordProcessing {

    private static final String[] WORDS_TO_OMIT = {"is", "a", "an", "the", "i", "we", "you", "rt", "have"};

    private final OpenNlpProvider openNlpProvider;
    private final int minWordLength;

    public WordProcessing(OpenNlpProvider openNlpProvider, int minWordLength) {
        this.openNlpProvider = openNlpProvider;
        this.minWordLength = minWordLength;
    }

    public List<String> getStemmedWords(String[] sentences) {
        List<String> stemmedWords = new ArrayList<>();

        for (String sentence : sentences) {
            List<String> words = openNlpProvider.filterOutNonWordsAndNouns(sentence);
            for (String word : words) {
                if (!wordToOmit(word)) {
                    String stemmedWord = openNlpProvider.stemWord(word).toLowerCase();
                    if (stemmedWord.length() >= minWordLength) {
                        stemmedWords.add(stemmedWord);
                    }
                }
            }
        }
        return stemmedWords;
    }

    public List<String> getLemmatizedWords(String[] sentences) {
        List<String> newWords = new ArrayList<>();

        Arrays.stream(sentences).forEach(sentence -> {
            List<String> words = openNlpProvider.lemmatizeSentence(sentence)
                    .stream()
                    .filter(w -> !wordToOmit(w))
                    .collect(Collectors.toList());
            newWords.addAll(words);
        });

        return newWords;
    }

    private boolean wordToOmit(String word) {
        for (String wordToOmit : WORDS_TO_OMIT) {
            if (word.equals(wordToOmit)) {
                return true;
            }
        }
        return word.length() < minWordLength;
    }
}

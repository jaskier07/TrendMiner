package pl.kania.trendminer.dataparser.parser;

import java.util.*;
import java.util.stream.Collectors;

public class WordProcessing {

    private static final Set<String> WORDS_TO_OMIT = new HashSet<>(Arrays.asList("me" , "my" , "myself" , "we" , "our" , "ours" , "ourselves" , "you" , "your" , "yours" , "yourself" , "yourselves" , "he" , "him" , "his" , "himself" , "she" , "her" , "hers" , "herself" , "it" , "its" , "itself" , "they" , "them" , "their" , "theirs" , "themselves" , "what" , "which" , "who" , "whom" , "this" , "that" , "these" , "those" , "am" , "is" , "are" , "was" , "were" , "be" , "been" , "being" , "have" , "has" , "had" , "having" , "do" , "does" , "did" , "doing" , "a" , "an" , "the" , "and" , "but" , "if" , "or" , "because" , "as" , "until" , "while" , "of" , "at" , "by" , "for" , "with" , "about" , "against" , "between" , "into" , "through" , "during" , "before" , "after" , "above" , "below" , "to" , "from" , "up" , "down" , "in" , "out" , "on" , "off" , "over" , "under" , "again" , "further" , "then" , "once" , "here" , "there" , "when" , "where" , "why" , "how" , "all" , "any" , "both" , "each" , "few" , "more" , "most" , "other" , "some" , "such" , "no" , "nor" , "not" , "only" , "own" , "same" , "so" , "than" , "too" , "very" , "s" , "t" , "can" , "will" , "just" , "don" , "should" , "now"));

    private final OpenNlpProvider openNlpProvider;
    private final int minWordLength;
    private final boolean improveResults;

    public WordProcessing(OpenNlpProvider openNlpProvider, int minWordLength, boolean improveResults) {
        this.openNlpProvider = openNlpProvider;
        this.minWordLength = minWordLength;
        this.improveResults = improveResults;
    }

    public List<String> perform(String[] sentences) {
        if (improveResults) {
            return getLemmatizedWords(sentences);
        }
        return getStemmedWords(sentences);
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
        if (WORDS_TO_OMIT.contains(word)) {
            return true;
        }
        if (improveResults) {
            return word.length() < minWordLength;
        }
        return false;
    }
}

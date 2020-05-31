package pl.kania.trendminer.parser;

import antlr.Token;
import lombok.extern.slf4j.Slf4j;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import pl.kania.trendminer.preproc.TweetContentTokenizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class OpenNlpProvider {

    private SentenceDetectorME sentenceDetector;
    private POSTagger posTagger;
    private Stemmer stemmer;

    public OpenNlpProvider() {
        initModel();
    }

    public String[] divideIntoSentences(String text) {
        return sentenceDetector.sentDetect(text);
    }

    public List<String> filterOutNonWordsAndNouns(String sentence) {
        String[] tokens = TweetContentTokenizer.tokenizeAndReturnArray(sentence);
        String[] tags = posTagger.tag(tokens);
        List<String> preservedWords = new ArrayList<>();

        for (int i = 0; i < tokens.length; i++) {
            if (isNounOrVerb(tags[i])) {
                preservedWords.add(tokens[i]);
            } else {
                log.debug("Dropped non-noun and non-verb word: " + tokens[i]);
            }
        }

        return preservedWords;
    }

    public String stemWord(String word) {
        return stemmer.stem(word).toString();
    }

    private boolean isNounOrVerb(String tag) {
        return isNoun(tag) || isVerb(tag);
    }

    private boolean isVerb(String tag) {
        return tag.equals("VB") || tag.equals("VBD") || tag.equals("VBZ");
    }

    private boolean isNoun(String tag) {
        return tag.equals("NN") || tag.equals("NNP");
    }

    private void initModel() {
        try (InputStream is = getClass().getResourceAsStream("/en-sent.bin")) {
            SentenceModel sentenceModel = new SentenceModel(is);
            sentenceDetector = new SentenceDetectorME(sentenceModel);
        } catch (IOException e) {
            log.error("Cannot load file containing English sentences", e);
        }

        try (InputStream is = getClass().getResourceAsStream("/en-pos-maxent.bin")) {
            POSModel posModel = new POSModel(is);
            posTagger = new POSTaggerME(posModel);
        } catch (IOException e) {
            log.error("Cannot load file containing English word mappings to part-of-speech.");
        }

        stemmer = new PorterStemmer();
    }
}

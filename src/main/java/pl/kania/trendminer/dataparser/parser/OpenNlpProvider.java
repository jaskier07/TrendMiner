package pl.kania.trendminer.dataparser.parser;

import lombok.extern.slf4j.Slf4j;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.stemmer.Stemmer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.kania.trendminer.dataparser.parser.preproc.filtering.TweetContentTokenizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OpenNlpProvider {

    public static final String UNKNOWN_LEMMA = "O";
    private SentenceDetectorME sentenceDetector;
    private POSTagger posTagger;
    private Stemmer stemmer;
    private DictionaryLemmatizer dictionaryLemmatizer;
    private Environment environment;

    public OpenNlpProvider(@Autowired Environment environment) {
        this.environment = environment;
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

    public List<String> lemmatizeSentence(String sentence) {
        String[] tokens = TweetContentTokenizer.tokenizeAndReturnArray(sentence);
        String[] tags = posTagger.tag(tokens);
        String[] lemmas = dictionaryLemmatizer.lemmatize(tokens, tags);
        List<String> words = new ArrayList<>();

        for (int i = 0; i < lemmas.length; i++) {
            if (isNounOrVerb(tags[i])) {
                if (lemmas[i].equals(UNKNOWN_LEMMA)) {
                    String stemmedWord = stemWord(tokens[i]);
                    words.add(stemmedWord);
                } else {
                    words.add(lemmas[i]);
                }
            }
        }

        return words;
    }

    private boolean isNounOrVerb(String tag) {
        return isNoun(tag) || isVerb(tag);
    }

    private boolean isVerb(String tag) {
        // verb (base form), verb (past tense), verb (3rd person singular present),
        return tag.startsWith("VB");//tag.equals("VB") || tag.equals("VBD") || tag.equals("VBZ") || tag.equals();
    }

    private boolean isNoun(String tag) {
        // noun (singular or mass), noun (proper noun, singular), noun (plural), noun (proper noun, plural)
        return tag.startsWith("NN"); //tag.equals("NN") || tag.equals("NNP") || tag.equals("NNS") || tag.equals("NNPS");
    }

    private void initModel() {
        try (InputStream is = getClass().getResourceAsStream(environment.getProperty("pl.kania.path.model.english-sentences"))) {
            SentenceModel sentenceModel = new SentenceModel(is);
            sentenceDetector = new SentenceDetectorME(sentenceModel);
        } catch (IOException e) {
            log.error("Cannot load file containing English sentences", e);
        }

        try (InputStream is = getClass().getResourceAsStream(environment.getProperty("pl.kania.path.model.parts-of-speech"))) {
            POSModel posModel = new POSModel(is);
            posTagger = new POSTaggerME(posModel);
        } catch (IOException e) {
            log.error("Cannot load file containing English word mappings to part-of-speech.");
        }

        try (InputStream is = getClass().getResourceAsStream(environment.getProperty("pl.kania.path.model.lemmatizer"))) {
            dictionaryLemmatizer = new DictionaryLemmatizer(is);
        } catch (IOException e) {
            log.error("Cannot load file containing English lemmatization mappings");
        }

        stemmer = new PorterStemmer();
    }
}

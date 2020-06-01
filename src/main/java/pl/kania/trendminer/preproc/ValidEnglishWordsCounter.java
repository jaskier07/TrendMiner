package pl.kania.trendminer.preproc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kania.trendminer.Tweet;
import pl.kania.trendminer.parser.OpenNlpProvider;

import java.util.List;

@Service
public class ValidEnglishWordsCounter {

    private Dictionary dictionary;
    private OpenNlpProvider openNlpProvider;

    public ValidEnglishWordsCounter(@Autowired Dictionary dictionary) {
        this.dictionary = dictionary;
        this.openNlpProvider = new OpenNlpProvider();
    }

    public int getPercentageOfEnglishWords(Tweet tweet) {
        List<String> words = TweetContentTokenizer.tokenize(tweet.getContent());
        if (words.isEmpty()) {
            return 0;
        }

        int englishWordsCount = words.stream()
                .map(String::toLowerCase)
                .map(dictionary::isEnglishWord)
                .map(t -> t ? 1 : 0)
                .reduce(Integer::sum)
                .orElseThrow(() -> new IllegalStateException("An error occurred while counting english words"));
        return englishWordsCount * 100 / words.size();
    }
}

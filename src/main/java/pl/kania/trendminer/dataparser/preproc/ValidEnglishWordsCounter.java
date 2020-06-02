package pl.kania.trendminer.dataparser.preproc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kania.trendminer.dataparser.Tweet;
import pl.kania.trendminer.dataparser.parser.OpenNlpProvider;

import java.util.List;

@Service
public class ValidEnglishWordsCounter {

    private Dictionary dictionary;

    public ValidEnglishWordsCounter(@Autowired Dictionary dictionary) {
        this.dictionary = dictionary;
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

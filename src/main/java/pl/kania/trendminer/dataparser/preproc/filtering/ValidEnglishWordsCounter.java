package pl.kania.trendminer.dataparser.preproc.filtering;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;
import pl.kania.trendminer.dataparser.Tweet;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class ValidEnglishWordsCounter {

    private final Dictionary dictionary;
    private final Pattern numberPattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    public ValidEnglishWordsCounter(@Autowired @Qualifier("wordnet") Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public int getPercentageOfEnglishWords(Tweet tweet) {
        List<String> words = TweetContentTokenizer.tokenize(tweet.getContent());
        words.removeIf(word -> numberPattern.matcher(word).matches());
        if (words.isEmpty()) {
            return 0;
        }

        int englishWordsCount = words.stream()
                .map(String::toLowerCase)
                .map(dictionary::isEnglishWord)
                .map(t -> t ? 1 : 0)
                .reduce(Integer::sum)
                .orElse(0);
        return englishWordsCount * 100 / words.size();
    }
}

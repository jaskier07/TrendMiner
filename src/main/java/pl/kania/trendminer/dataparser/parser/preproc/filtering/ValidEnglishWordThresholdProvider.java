package pl.kania.trendminer.dataparser.parser.preproc.filtering;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kania.trendminer.dataparser.Tweet;
import pl.kania.trendminer.dataparser.parser.location.EnglishSpeakingCountryDetector;

@Service
public class ValidEnglishWordThresholdProvider {

    private static final String EN = "en";
    private static final int LOW_THRESHOLD = 50;
    private static final int MEDIUM_THRESHOLD = 70;
    private static final int HIGH_THRESHOLD = 90;

    private final EnglishSpeakingCountryDetector detector;
    @Getter
    private static int tweetsWithLocation = 0;

    public ValidEnglishWordThresholdProvider(@Autowired EnglishSpeakingCountryDetector englishSpeakingCountryDetector) {
        this.detector = englishSpeakingCountryDetector;
    }

    public int getThresholdInPercentage(Tweet tweet) {
        boolean languageEn = tweet.getLang().equals(EN);
        boolean englishSpeakingCountry = isEnglishSpeakingCountry(tweet.getLocation());
        if (englishSpeakingCountry) {
            tweetsWithLocation++;
        }
        if (languageEn && englishSpeakingCountry) {
            return LOW_THRESHOLD;
        } else if (languageEn || englishSpeakingCountry) {
            return MEDIUM_THRESHOLD;
        }
        return HIGH_THRESHOLD;
    }

    private boolean isEnglishSpeakingCountry(String location) {
        return detector.isInEnglishSpeakingCountry(location);
    }
}

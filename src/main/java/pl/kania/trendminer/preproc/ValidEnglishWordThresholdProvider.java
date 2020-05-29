package pl.kania.trendminer.preproc;

import org.springframework.stereotype.Service;
import pl.kania.trendminer.model.Tweet;

@Service
public class ValidEnglishWordThresholdProvider {

    private static final String EN = "en";
    private static final int LOW_THRESHOLD = 50;
    private static final int MEDIUM_THRESHOLD = 80;
    private static final int HIGH_THRESHOLD = 90;

    public int getThresholdInPercentage(Tweet tweet) {
        boolean languageEn = tweet.getLang().equals(EN);
        boolean englishSpeakingCountry = isEnglishSpeakingCountry(tweet.getLocation());
        if (languageEn && englishSpeakingCountry) {
            return LOW_THRESHOLD;
        } else if (languageEn || englishSpeakingCountry) {
            return MEDIUM_THRESHOLD;
        }
        return HIGH_THRESHOLD;
    }

    private boolean isEnglishSpeakingCountry(String location) {
        // TOOD
        return false;
    }
}

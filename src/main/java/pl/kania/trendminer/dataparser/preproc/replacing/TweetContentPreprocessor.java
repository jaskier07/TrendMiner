package pl.kania.trendminer.dataparser.preproc.replacing;

import pl.kania.trendminer.dataparser.Tweet;

public class TweetContentPreprocessor {

    public void performPreprocessing(Tweet tweet) {
        String content = tweet.getContent();
        content = MentionReplacer.removeMentions(content);
        content = ShortcutReplacer.replaceShortcuts(content);
        content = HashtagReplacer.replaceHashtags(content);
        content = SpecialCharactersReplacer.replace(content);
        tweet.setContent(content);
    }
}

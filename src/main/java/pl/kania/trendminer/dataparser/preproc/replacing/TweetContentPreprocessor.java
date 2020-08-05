package pl.kania.trendminer.dataparser.preproc.replacing;

import pl.kania.trendminer.dataparser.Tweet;

public class TweetContentPreprocessor {

    public void performPreprocessing(Tweet tweet) {
        String content = tweet.getContent();
        content = MentionRemover.removeMentions(content);
        content = ShortcutReplacer.replace(content);
        content = HashtagReplacer.replace(content);
        content = LinkRemover.remove(content);
        content = SpecialCharactersRemover.remove(content);
        tweet.setContent(content);
    }
}

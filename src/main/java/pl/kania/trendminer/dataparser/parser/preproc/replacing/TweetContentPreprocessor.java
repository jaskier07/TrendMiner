package pl.kania.trendminer.dataparser.parser.preproc.replacing;

import pl.kania.trendminer.dataparser.Tweet;

public class TweetContentPreprocessor {

    public void performPreprocessing(Tweet tweet, boolean improveResults) {
        String content = tweet.getContent();
        content = MentionRemover.removeMentions(content);
        if (improveResults) {
            content = ShortcutReplacer.replace(content);
            content = HashtagReplacer.replace(content);
        }
        content = LinkRemover.remove(content);
        if (improveResults) {
            content = SpecialCharactersRemover.remove(content);
        }
        tweet.setContent(content);
    }
}

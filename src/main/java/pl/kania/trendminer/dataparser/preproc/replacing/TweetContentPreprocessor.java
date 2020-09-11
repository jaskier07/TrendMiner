package pl.kania.trendminer.dataparser.preproc.replacing;

import pl.kania.trendminer.dataparser.Tweet;
import pl.kania.trendminer.dataparser.parser.ImproveResults;

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

package pl.kania.trendminer.dataparser.preproc;

import pl.kania.trendminer.dataparser.Tweet;

public class TweetContentPreprocessor {

    public void performPreprocessing(Tweet tweet) {
        String content = tweet.getContent();
        content = removeMentions(content);
        content = replaceShortcuts(content);
        content = HashtagReplacer.replaceHashtags(content);
        tweet.setContent(content);
    }

    private String removeMentions(String text) {
        text = text.replaceAll("RT @(.*):", "");
        text = text.replaceAll("@[^\\s]*", "");
        return text;
    }


    private static String replaceShortcuts(String content) {
        content = content.toLowerCase();

        content = content.replaceAll("’", "'");
        content = content.replaceAll("what's", "what is ");
        content = content.replaceAll("'s", " ");
        content = content.replaceAll("'ve", " have ");
        content = content.replaceAll("can't", "can not ");
        content = content.replaceAll("won't", "will not ");
        content = content.replaceAll("n't", " not ");
        content = content.replaceAll("i'm", "i am ");
        content = content.replaceAll("'re", " are ");
        content = content.replaceAll("'d", " would ");
        content = content.replaceAll("'ll", " will ");
        content = content.replaceAll("'scuse", " excuse ");

        return content;
    }
}

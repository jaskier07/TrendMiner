package pl.kania.trendminer.dataparser.preproc.replacing;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MentionReplacer {

    public static String removeMentions(String text) {
        text = text.replaceAll("RT @(.*?):", "");
        text = text.replaceAll("@[^\\s]*", "");
        return text;
    }
}

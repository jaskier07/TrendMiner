package pl.kania.trendminer.dataparser.preproc.replacing;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ShortcutReplacer {

    public static String replaceShortcuts(String content) {
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

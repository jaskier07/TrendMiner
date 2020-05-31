package pl.kania.trendminer.preproc;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TweetContentTokenizer {

    private static final String DELIMETERS = " ,.;:/\\[]()!\'\"|?<>+=";

    public static List<String> tokenize(String content) {
        content = replaceShortcuts(content);

        StringTokenizer tokenizer = new StringTokenizer(content, DELIMETERS);
        List<String> tokens = new ArrayList<>();

        while (tokenizer.hasMoreElements()) {
            String token = tokenizer.nextToken();
            if (Strings.isNotBlank(token) && !isDelimiter(token.trim())) {
                tokens.add(token);
            }
        }

        return tokens;
    }

    private static String replaceShortcuts(String content) {
        content = content.toLowerCase();;
        content = content.replaceAll("what's", "what is ");
        content = content.replaceAll("'s", " ");
        content = content.replaceAll("'ve", " have ");
        content = content.replaceAll("can't", "can not ");
        content = content.replaceAll("n't", " not ");
        content = content.replaceAll("i'm", "i am ");
        content = content.replaceAll("'re", " are ");
        content = content.replaceAll("'d", " would ");
        content = content.replaceAll("'ll", " will ");
        content = content.replaceAll("'scuse", " excuse ");
        return content;
    }

    public static String[] tokenizeAndReturnArray(String content) {
        return tokenize(content).toArray(new String[0]);
    }

    private static boolean isDelimiter(String s) {
        return DELIMETERS.contains(s);
    }
}

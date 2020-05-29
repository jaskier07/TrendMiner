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
        StringTokenizer tokenizer = new StringTokenizer(content, DELIMETERS);
        List<String> tokens = new ArrayList<>();

        while (tokenizer.hasMoreElements()) {
            String token = tokenizer.nextToken();
            if (Strings.isNotBlank(token) && !isDelimeter(token.trim())) {
                tokens.add(token);
            }
        }

        return tokens;
    }

    private static boolean isDelimeter(String s) {
        return DELIMETERS.contains(s);
    }
}

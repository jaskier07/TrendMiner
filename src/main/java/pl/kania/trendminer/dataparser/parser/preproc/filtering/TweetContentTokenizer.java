package pl.kania.trendminer.dataparser.parser.preproc.filtering;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TweetContentTokenizer {

    private static final String DELIMITERS = "!\"$%&\\'()*+,.:;<=>?@[]^_`{|}~ …”‘’“\r\n#";

    public static List<String> tokenize(String content) {
        StringTokenizer tokenizer = new StringTokenizer(content, DELIMITERS);
        List<String> tokens = new ArrayList<>();

        while (tokenizer.hasMoreElements()) {
            String token = tokenizer.nextToken();
            if (Strings.isNotBlank(token) && !isDelimiter(token.trim())) {
                tokens.add(token);
            } else {
                log.debug("Dropped token: " + token);
            }
        }

        return tokens;
    }

    public static String[] tokenizeAndReturnArray(String content) {
        return tokenize(content).toArray(new String[0]);
    }

    private static boolean isDelimiter(String s) {
        return DELIMITERS.contains(s);
    }
}

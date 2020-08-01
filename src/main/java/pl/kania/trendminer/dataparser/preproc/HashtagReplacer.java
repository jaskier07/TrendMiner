package pl.kania.trendminer.dataparser.preproc;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.jbosslog.JBossLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@JBossLog
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HashtagReplacer {

    private static final Pattern pattern = Pattern.compile("#\\S+");

    public static String replaceHashtags(String text) {
        Matcher matcher = pattern.matcher(text);
        String newText = text;

        try {
            while (matcher.find()) {
                newText = matcher.replaceAll(splitHashtagIntoWords());
            }
        } catch (Exception e) {
            log.warn("Problem with hashtag in text " + text + ". Problem: " + e.getMessage());
        }

        return newText;
    }

    private static Function<MatchResult, String> splitHashtagIntoWords() {
        return matchResult -> {
            String newText = "";
            char[] hashtag = matchResult.group(0).replace("#", "").toCharArray();

            boolean previousBigLetter = false;
            for (int i = 1; i < hashtag.length + 1; i++) {
                char previous = hashtag[i - 1];
                if (i != hashtag.length) {
                    char current = hashtag[i];

                    if (Character.isLetter(current)) {
                        if (previousBigLetter && Character.isLowerCase(current)) {
                            newText += " " + Character.toLowerCase(previous);
                        } else {
                            newText += Character.toLowerCase(previous);
                        }
                        previousBigLetter = Character.isUpperCase(current);
                    } else {
                        previousBigLetter = false;
                        newText += Character.toLowerCase(previous);
                    }
                } else {
                    newText += Character.toLowerCase(previous);
                }
            }


            // jak wielkie litery jedna po drugiej to nie dziel

//            String[] split = matchResult.group(0).split("(?=\\p{Lu})");
//            return String.join(" ", split).replace("#", "").toLowerCase().trim();
            return newText;
        };
    }

    private static boolean isSingleWord(char c) {
        c = Character.toLowerCase(c);
        return c == 'i' || c == 'a';
    }
}

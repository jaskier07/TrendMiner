package pl.kania.trendminer.dataparser.preproc.replacing;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpecialCharactersReplacer {
    private static final Pattern pattern = Pattern.compile("[^\\p{L}\\p{N}\\p{P}\\p{Z}]", Pattern.UNICODE_CHARACTER_CLASS);

    public static String replace(String text) {
        Matcher matcher = pattern.matcher(text);
        return matcher.replaceAll(" ");
    }
}

package pl.kania.trendminer.dataparser.parser.preproc.replacing;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpecialCharactersRemover {
    private static final Pattern pattern = Pattern.compile("[^\\p{L}\\p{N}\\p{P}\\p{Z}]", Pattern.UNICODE_CHARACTER_CLASS);

    public static String remove(String text) {
        Matcher matcher = pattern.matcher(text);
        return matcher.replaceAll(" ");
    }
}

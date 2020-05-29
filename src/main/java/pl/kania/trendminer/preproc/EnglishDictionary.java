package pl.kania.trendminer.preproc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class EnglishDictionary implements Dictionary {

    public static final String PATH_TO_DICT = "/src/main/resources/words_alpha.txt";
    private final Set<String> dictionary;

    public EnglishDictionary() {
        dictionary = new HashSet<>();
        initDictionary();
    }

    public boolean isEnglishWord(String word) {
        return dictionary.contains(word);
    }

    public int getSize() {
        return dictionary.size();
    }

    private void initDictionary() {
        try {
            // TODO find more representative dict
            dictionary.addAll(Files.readAllLines(getPathToDict()));
        } catch (IOException e) {
            log.error("Error reading file with English words", e);
        }
    }

    private Path getPathToDict() {
        return Paths.get(System.getProperty("user.dir") + PATH_TO_DICT);
    }
}

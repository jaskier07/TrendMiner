package pl.kania.trendminer.dataparser.preproc.filtering;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service("file")
public class EnglishDictionaryFile implements Dictionary {

    private final Set<String> dictionary;
    private Environment environment;

    public EnglishDictionaryFile(@Autowired Environment environment) {
        dictionary = new HashSet<>();
        this.environment = environment;
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
        return new File(environment.getProperty("pl.kania.path.english-dictionary")).toPath();
    }
}

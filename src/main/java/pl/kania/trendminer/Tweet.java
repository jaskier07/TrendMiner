package pl.kania.trendminer;

import lombok.Data;
import pl.kania.trendminer.preproc.TweetContentTokenizer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Tweet {
    private String id;
    private String lang;
    private String author;
    private String location;
    private String content;
    private LocalDateTime createdAt;
    private List<String> words;
    private Set<String> stemmedWords = new HashSet<>();

    public Tweet(String id, String lang, String author, String location, String content, LocalDateTime createdAt) {
        this.id = id;
        this.lang = lang;
        this.author = author;
        this.location = location;
        this.content = content;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Content: " + getWords().toString() + ", author:" + author;
    }

    public List<String> getWords() {
        if (words == null) {
            words = TweetContentTokenizer.tokenize(content);
        }
        return words;
    }
}

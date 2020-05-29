package pl.kania.trendminer.model;

import lombok.Data;
import pl.kania.trendminer.preproc.TweetContentTokenizer;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Tweet {
    private String id;
    private String lang;
    private String author;
    private String location;
    private String content;
    private LocalDateTime createdAt;
    private List<String> words;

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

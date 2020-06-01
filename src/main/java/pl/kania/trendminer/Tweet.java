package pl.kania.trendminer;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.kania.trendminer.preproc.TweetContentTokenizer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class Tweet {
    private String id;
    private String lang;
    private String author;
    private String location;
    private String content;
    private LocalDateTime createdAt;
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
        return "Content: " + content + ", author:" + author;
    }
}

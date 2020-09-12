package pl.kania.trendminer.dataparser;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
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

    public Tweet(String id, String lang, String author, String location, String content, LocalDateTime createdAt) {
        this.id = id;
        this.lang = lang;
        this.author = author;
        this.location = location;
        this.content = content.toLowerCase();
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Content: " + content + ", author:" + author;
    }
}

package pl.kania.trendminer.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "word")
@Entity
public class Word implements Comparable<Word> {

    @Id
    @Setter
    @GeneratedValue
    private Long id;

    @Column(name = "WORD")
    private String word;

    public Word(String word) {
        this.word = word;
    }

    @Override
    public String toString() {
        return word;
    }

    @Override
    public int compareTo(Word other) {
        if (other == null || other.getWord() == null) {
            return -1;
        } else if (word == null) {
            return 1;
        }
        return getId() > other.getId() ? 1 : -1;
    }
}

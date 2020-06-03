package pl.kania.trendminer.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cooccurrence {

    @Id
    @GeneratedValue
    private Long id;

    @EqualsAndHashCode.Include
    @ManyToOne
    @JoinColumn(name = "WORD_1_ID")
    private Word word1;

    @EqualsAndHashCode.Include
    @ManyToOne
    @JoinColumn(name = "WORD_2_ID")
    private Word word2;

    @Column(name = "SUPPORT")
    private Double support;

    @EqualsAndHashCode.Include
    @ManyToOne
    @JoinColumn(name = "TIME_ID")
    private TimeId timeID;

    public Cooccurrence(Word word1, Word word2, TimeId timeID, Double support) {
        this.word1=  word1;
        this.word2 = word2;
        this.timeID = timeID;
        this.support = support;
    }

    @Override
    public String toString() {
        return "[" + word1 + ", " + word2 + "]";
    }

    public List<Word> getWords() {
        return List.of(word1, word2);
    }
}

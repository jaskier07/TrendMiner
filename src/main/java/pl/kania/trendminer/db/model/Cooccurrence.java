package pl.kania.trendminer.db.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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

    @ManyToOne
    @JoinColumn(name = "WORD_1_ID")
    @EqualsAndHashCode.Include
    private Word word1;

    @ManyToOne
    @JoinColumn(name = "WORD_2_ID")
    @EqualsAndHashCode.Include
    private Word word2;

    @Column(name = "SUPPORT")
    private Double support;

    @ManyToOne
    @JoinColumn(name = "TIME_ID")
    @EqualsAndHashCode.Include
    private TimeId timeID;

    public Cooccurrence(Word word1, Word word2, TimeId timeID, Double support) {
        this(word1, word2, timeID);
        this.support = support;
    }

    public Cooccurrence(Word word1, Word word2, TimeId timeID) {
        setWordsInOrder(word1, word2);
        this.timeID = timeID;
    }

    private void setWordsInOrder(Word word1, Word word2) {
        if (word1.compareTo(word2) > 0) {
            this.word1 = word2;
            this.word2 = word1;
        } else {
            this.word1 = word1;
            this.word2 = word2;
        }
    }

    @Override
    public String toString() {
        return "[" + word1 + ", " + word2 + "]";
    }

    public List<Word> getWords() {
        return List.of(word1, word2);
    }

    public long getFrequency(Long periodFrequency) {
        return (long)(support * periodFrequency);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Cooccurrence;
    }

}

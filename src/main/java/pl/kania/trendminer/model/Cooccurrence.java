package pl.kania.trendminer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Cooccurrence {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "WORD_1_ID")
    private Word word1;

    @ManyToOne
    @JoinColumn(name = "WORD_2_ID")
    private Word word2;

    @Column(name = "SUPPORT")
    private Double support;

    @ManyToOne
    @JoinColumn(name = "TIME_ID")
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

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Cooccurrence)) return false;
        final Cooccurrence other = (Cooccurrence) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$word1 = this.getWord1();
        final Object other$word1 = other.getWord1();
        if (this$word1 == null ? other$word1 != null : !this$word1.equals(other$word1)) return false;
        final Object this$word2 = this.getWord2();
        final Object other$word2 = other.getWord2();
        if (this$word2 == null ? other$word2 != null : !this$word2.equals(other$word2)) return false;
        final Object this$timeID = this.getTimeID();
        final Object other$timeID = other.getTimeID();
        if (this$timeID == null ? other$timeID != null : !this$timeID.equals(other$timeID)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Cooccurrence;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $word1 = this.getWord1();
        result = result * PRIME + ($word1 == null ? 43 : $word1.hashCode());
        final Object $word2 = this.getWord2();
        result = result * PRIME + ($word2 == null ? 43 : $word2.hashCode());
        final Object $timeID = this.getTimeID();
        result = result * PRIME + ($timeID == null ? 43 : $timeID.hashCode());
        return result;
    }
}

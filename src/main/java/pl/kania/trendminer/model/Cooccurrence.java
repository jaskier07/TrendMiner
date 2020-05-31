package pl.kania.trendminer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.mapping.ToOne;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
    private TimeID timeID;

    public Cooccurrence(Word word1, Word word2, TimeID timeID, Double support) {
        this.word1=  word1;
        this.word2 = word2;
        this.timeID = timeID;
        this.support = support;
    }
}

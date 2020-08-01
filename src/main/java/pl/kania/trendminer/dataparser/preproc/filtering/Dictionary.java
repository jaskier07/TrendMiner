package pl.kania.trendminer.dataparser.preproc.filtering;

public interface Dictionary {

    boolean isEnglishWord(String word);

    int getSize();
}

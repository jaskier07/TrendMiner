package pl.kania.trendminer.dataparser.preproc;

public interface Dictionary {

    boolean isEnglishWord(String word);

    int getSize();
}

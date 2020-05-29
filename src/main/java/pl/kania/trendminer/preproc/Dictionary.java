package pl.kania.trendminer.preproc;

public interface Dictionary {

    boolean isEnglishWord(String word);

    int getSize();
}

package pl.kania.trendminer.dataparser.parser.preproc.filtering;

public interface Dictionary {

    boolean isEnglishWord(String word);

    int getSize();
}

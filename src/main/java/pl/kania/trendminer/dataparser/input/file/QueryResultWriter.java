package pl.kania.trendminer.dataparser.input.file;

import pl.kania.trendminer.dataparser.input.FileOutputProvider;
import pl.kania.trendminer.dataparser.input.Writable;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class QueryResultWriter extends Writable {

    private final FileOutputProvider fop;
    private final StringBuilder textToWrite;

    public QueryResultWriter(FileOutputProvider fop) {
        this.fop = fop;
        this.textToWrite = new StringBuilder();
    }

    public void appendText(String text) {
        textToWrite.append(text);
    }

    @Override
    protected void writeToFile(FileWriter fw) throws IOException {
        fw.write(textToWrite.toString());
    }

    @Override
    protected FileOutputProvider getFileOutputProvider() {
        return fop;
    }

    @Override
    protected String getFilename() {
        return "Result";
    }

    @Override
    protected String getHeader() {
        return "from,to,index,value,word1,word2,word3,word4,word5,word6,word7,word8,word9,word10\n";
    }
}

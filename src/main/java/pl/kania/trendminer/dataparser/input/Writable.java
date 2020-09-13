package pl.kania.trendminer.dataparser.input;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
public abstract class Writable {

    protected abstract FileOutputProvider getFileOutputProvider();

    protected abstract String getFilename();

    protected abstract String getHeader();

    protected abstract void writeToFile(FileWriter fw) throws IOException;

    public void write() {
        FileOutputProvider fop = getFileOutputProvider();

        Filepath filepath = fop.get(getFilename());
        File file = new File(filepath.getPath(), filepath.getFilename());
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(getHeader());
            writeToFile(fw);
        } catch (IOException e) {
            log.error("Error creating file " + filepath.getFilename(), e);
        }
    }
}


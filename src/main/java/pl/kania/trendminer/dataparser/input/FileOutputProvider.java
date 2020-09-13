package pl.kania.trendminer.dataparser.input;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileOutputProvider {

    private final String pathToFolder;
    private final DateTimeFormatter dtf;

    public FileOutputProvider(String pathToDataset) {
        this.pathToFolder = pathToDataset;
        dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
    }

    public Filepath get(String name) {
        String formattedDate = dtf.format(LocalDateTime.now());
        return new Filepath(formattedDate + "_" + name + ".csv", pathToFolder);
    }
}


package pl.kania.trendminer.dataparser.parser;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class ImproveResults {

    private final boolean value;

    public ImproveResults(@Autowired Environment env) {
        value = Boolean.parseBoolean(env.getProperty("pl.kania.improve-results"));
    }

    public boolean get() {
        return value;
    }
}

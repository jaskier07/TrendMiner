package pl.kania.trendminer;

public class ParserExecutionException extends RuntimeException {

    public ParserExecutionException(String message) {
        super("Fatal app error occured. Reason: " + message);
    }
}

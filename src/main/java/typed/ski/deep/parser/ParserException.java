package typed.ski.deep.parser;

public class ParserException extends Exception {

    public ParserException(String message) {
        super(message);
    }

    public ParserException(Throwable cause) {
        super(cause);
    }

    public ParserException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

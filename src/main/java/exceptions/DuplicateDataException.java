package exceptions;

public class DuplicateDataException extends RuntimeException {
    public DuplicateDataException(String message, Throwable cause) {
        super(message, cause);
    }
}

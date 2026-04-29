package hexlet.code.app.exception;

public final class UnableToDeleteException extends RuntimeException {
    public UnableToDeleteException(final String message) {
        super(message);
    }

    public UnableToDeleteException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

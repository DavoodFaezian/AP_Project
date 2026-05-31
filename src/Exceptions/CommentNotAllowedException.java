package Exceptions;

public class CommentNotAllowedException extends RuntimeException {
    public CommentNotAllowedException(String message) {
        super(message);
    }
}

package Exceptions;

public class PasswordNotLongEnoughException extends RuntimeException {
    public PasswordNotLongEnoughException(String message) {
        super(message);
    }
}

package Exceptions;

public class PasswordNotStrongException extends RuntimeException {
    public PasswordNotStrongException(String message) {
        super(message);
    }
}

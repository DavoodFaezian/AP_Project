package Exceptions;

public class PasswordContainsUserNameException extends RuntimeException {
    public PasswordContainsUserNameException(String message) {
        super(message);
    }
}

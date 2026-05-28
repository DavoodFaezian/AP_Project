package Exceptions;

public class PhotoDoesNotExistException extends RuntimeException {
    public PhotoDoesNotExistException(String message){
        super(message);
    }
}

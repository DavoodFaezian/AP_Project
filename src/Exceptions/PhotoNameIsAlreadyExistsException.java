package Exceptions;

public class PhotoNameIsAlreadyExistsException extends RuntimeException{
    public PhotoNameIsAlreadyExistsException(String message){
        super(message);
    }
}

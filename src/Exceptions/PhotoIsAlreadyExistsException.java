package Exceptions;

public class PhotoIsAlreadyExistsException extends RuntimeException{
    public PhotoIsAlreadyExistsException(String message){
        super(message);
    }
}

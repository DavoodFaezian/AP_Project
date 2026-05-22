package Exceptions;

public class AlbumNameIsAlreadyExistsException extends RuntimeException{
    public AlbumNameIsAlreadyExistsException(String message){
        super(message);
    }
}

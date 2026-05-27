package Exceptions;

public class PhotoIsAlreadyExistsInThisAlbumException extends RuntimeException{
    public PhotoIsAlreadyExistsInThisAlbumException(String message){
        super(message);
    }
}

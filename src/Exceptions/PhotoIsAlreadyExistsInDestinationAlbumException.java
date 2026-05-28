package Exceptions;

public class PhotoIsAlreadyExistsInDestinationAlbumException extends RuntimeException{
    public PhotoIsAlreadyExistsInDestinationAlbumException(String message){
        super(message);
    }
}

package Exceptions;

public class PhotoDoesNotExistInOriginalAlbumException extends RuntimeException{
    public PhotoDoesNotExistInOriginalAlbumException(String message){
        super(message);
    }
}

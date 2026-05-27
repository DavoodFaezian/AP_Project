package Exceptions;

public class NotOwnersAlbumException extends RuntimeException{
    public NotOwnersAlbumException(String message){
        super(message);
    }
}

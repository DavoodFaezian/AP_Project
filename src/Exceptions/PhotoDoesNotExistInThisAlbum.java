package Exceptions;

public class PhotoDoesNotExistInThisAlbum extends RuntimeException{
    public PhotoDoesNotExistInThisAlbum(String message){
        super(message);
    }
}

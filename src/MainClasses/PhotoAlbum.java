package MainClasses;

import Exceptions.ItemDoesNotExistException;
import Exceptions.PhotoIsAlreadyExistsException;

public class PhotoAlbum{

    private void validatePhoto(Photo photo){
        if(photo == null){
            throw new NullPointerException("Photo is null!!!");
        }
    }

    private void validateAddToNull(Photo photo){
        if(photo.getAlbums().contains(null)){
            throw new PhotoIsAlreadyExistsException("Photo is already exists!!!");
        }
    }

    private void validateToAdd(Album album , Photo photo){
        if(album.getPhotos().contains(photo)){
            throw new PhotoIsAlreadyExistsException("Photo is already exists!!!");
        }
    }

    public void addPhotoToAlbum(Photo photo , Album album){
        validatePhoto(photo);
        if (album != null) {
            validateToAdd(album , photo);
            album.getPhotos().add(photo);
            photo.getAlbums().add(album);
            album.updateTime();
        }
        else {
            validateAddToNull(photo);
            photo.getAlbums().add(null);
        }
    }

    private void validateToRemove(Album album , Photo photo){
        if(!album.getPhotos().contains(photo)){
            throw new ItemDoesNotExistException("Photo does not exist!!!");
        }
    }

    private void validateRemoveFromNull(Photo photo){
        if(!photo.getAlbums().contains(null)){
            throw new ItemDoesNotExistException("Photo does not exist!!!");
        }
    }

    public void removePhotoFromAlbum(Photo photo , Album album){
        validatePhoto(photo);
        if(album != null){
            validateToRemove(album , photo);
            album.getPhotos().remove(photo);
            photo.getAlbums().remove(album);
            album.updateTime();
        }
        else {
            validateRemoveFromNull(photo);
            photo.getAlbums().remove(null);
        }
        if(photo.getAlbums().isEmpty()){
            photo.getOwner().getPhotos().remove(photo);
        }
    }

    public void transferPhoto(Photo photo , Album fromAlbum , Album toAlbum){
        validatePhoto(photo);
        if(fromAlbum != null && toAlbum != null){
            validateToAdd(toAlbum , photo);
            validateToRemove(fromAlbum , photo);
            fromAlbum.getPhotos().remove(photo);
            toAlbum.getPhotos().add(photo);
            fromAlbum.updateTime();
            toAlbum.updateTime();
        } else if (fromAlbum != null){
            validateToRemove(fromAlbum , photo);
            fromAlbum.getPhotos().remove(photo);
            fromAlbum.updateTime();
        } else if(toAlbum != null){
            validateToAdd(toAlbum , photo);
            toAlbum.getPhotos().add(photo);
            toAlbum.updateTime();
        }
        photo.getAlbums().remove(fromAlbum);
        photo.getAlbums().add(toAlbum);
    }

    public void sharePhoto(Photo photo, User user1, User user2) {
    }
}
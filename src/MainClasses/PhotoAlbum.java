package MainClasses;

import Exceptions.ItemDoesNotExistException;
import Exceptions.PhotoIsAlreadyExistsException;

public class PhotoAlbum{

    private void validatePhoto(Photo photo){
        if(photo == null){
            throw new NullPointerException("Photo is null!!!");
        }
    }

    public void addPhotoToAlbum(Photo photo , Album album){
        validatePhoto(photo);
        if (album != null) {
            album.getPhotos().add(photo);
            photo.getAlbums().add(album);
            album.updateTime();
        }
        else {
            photo.getAlbums().add(null);
        }
    }

    public void removePhotoFromAlbum(Photo photo , Album album){
        validatePhoto(photo);
        if(album != null){
            album.getPhotos().remove(photo);
            photo.getAlbums().remove(album);
            album.updateTime();
        }
        else {
            photo.getAlbums().remove(null);
        }
        if(photo.getAlbums().isEmpty()){
            photo.getOwner().getPhotos().remove(photo);
        }
    }

    public void validateToRemove(Album album , Photo photo){
        if(!album.getPhotos().contains(photo)){
            throw new ItemDoesNotExistException("Photo wasn't found!!!");
        }
    }

    public void validateRemoveFromNull(Photo photo){
        if(!photo.getAlbums().contains(null)){
            throw new ItemDoesNotExistException("Photo wasn't found!!!");
        }
    }

    public void transferPhoto(Photo photo , Album fromAlbum , Album toAlbum){
        validatePhoto(photo);
        if(fromAlbum != null && toAlbum != null){
            validateToRemove(fromAlbum , photo);
            fromAlbum.getPhotos().remove(photo);
            toAlbum.getPhotos().add(photo);
            fromAlbum.updateTime();
            toAlbum.updateTime();
        } else if (fromAlbum != null){
            validateToRemove(fromAlbum , photo);
            fromAlbum.getPhotos().remove(photo);
            fromAlbum.updateTime();
        } else if(toAlbum != null) {
            validateRemoveFromNull(photo);
            toAlbum.getPhotos().add(photo);
            toAlbum.updateTime();
        } else {
            validateRemoveFromNull(photo);
        }
        photo.getAlbums().remove(fromAlbum);
        photo.getAlbums().add(toAlbum);
    }

    private void validateUser(User user){
        if(user == null){
            throw new NullPointerException("User is null!!!");
        }
    }

    public void sharePhoto(Photo photo , User sender , User receiver){
        validateUser(sender);
        validateUser(receiver);
        validatePhoto(photo);
        photo.getSharedWithUsers().add(receiver);
        receiver.getPhotos().add(photo);
        photo.updateDateOfShare();
    }

    public void undoSharePhoto(Photo photo , User sender , User receiver){
        validateUser(sender);
        validateUser(receiver);
        validatePhoto(photo);
        photo.getSharedWithUsers().remove(receiver);
    }
}
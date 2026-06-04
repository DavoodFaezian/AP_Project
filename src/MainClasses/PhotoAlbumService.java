package MainClasses;

import Exceptions.AccessDeniedException;
import Exceptions.ItemDoesNotExistException;

public class PhotoAlbumService{

    private void validateAccess(Photo photo , Album album){
        if(!photo.getOwner().equals(album.getOwner())){
            throw new AccessDeniedException("Access Denied!!!");
        }
    }

    private void validatePhoto(Photo photo){
        if(photo == null){
            throw new NullPointerException("Photo is null!!!");
        }
    }

    public void addPhotoToAlbum(Photo photo , Album album){
        validatePhoto(photo);
        if (album != null) {
            validateAccess(photo , album);
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
            validateAccess(photo , album);
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
            validateAccess(photo , fromAlbum);
            validateAccess(photo , toAlbum);
            validateToRemove(fromAlbum , photo);
            fromAlbum.getPhotos().remove(photo);
            toAlbum.getPhotos().add(photo);
            fromAlbum.updateTime();
            toAlbum.updateTime();
        } else if (fromAlbum != null){
            validateAccess(photo , fromAlbum);
            validateToRemove(fromAlbum , photo);
            fromAlbum.getPhotos().remove(photo);
            fromAlbum.updateTime();
        } else if(toAlbum != null) {
            validateAccess(photo , toAlbum);
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

    private void validateAccess(Photo photo , User user){
        if(!photo.getOwner().equals(user)){
            throw new AccessDeniedException("Access Denied!!!");
        }
    }

    public void sharePhoto(Photo photo , User sender , User receiver){
        validateUser(sender);
        validateUser(receiver);
        validatePhoto(photo);
        validateAccess(photo , sender);
        receiver.getSharedPhotos().add(photo);
        photo.getSharedWithUsers().add(receiver);
        photo.updateDateOfShare();
    }

    public void undoSharePhoto(Photo photo , User sender , User receiver){
        validateUser(sender);
        validateUser(receiver);
        validatePhoto(photo);
        validateAccess(photo , sender);
        receiver.getSharedPhotos().remove(photo);
        photo.getSharedWithUsers().remove(receiver);
    }
}
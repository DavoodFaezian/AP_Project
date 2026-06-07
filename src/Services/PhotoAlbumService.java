package Services;

import Exceptions.AccessDeniedException;
import Exceptions.ItemDoesNotExistException;
import MainClasses.Album;
import MainClasses.Photo;
import Repositories.AlbumRepository;
import Repositories.PhotoRepository;

import java.util.ArrayList;

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
            album.getPhotoAlbums().add(photo);
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
            album.getPhotoAlbums().remove(photo);
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
        if(!album.getPhotoAlbums().contains(photo)){
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
            fromAlbum.getPhotoAlbums().remove(photo);
            toAlbum.getPhotoAlbums().add(photo);
            fromAlbum.updateTime();
            toAlbum.updateTime();
        } else if (fromAlbum != null){
            validateAccess(photo , fromAlbum);
            validateToRemove(fromAlbum , photo);
            fromAlbum.getPhotoAlbums().remove(photo);
            fromAlbum.updateTime();
        } else if(toAlbum != null) {
            validateAccess(photo , toAlbum);
            validateRemoveFromNull(photo);
            toAlbum.getPhotoAlbums().add(photo);
            toAlbum.updateTime();
        } else {
            validateRemoveFromNull(photo);
        }
        photo.getAlbums().remove(fromAlbum);
        photo.getAlbums().add(toAlbum);
    }

    public ArrayList<Photo> getPhotosByAlbumId(String albumId){
        Album album = AlbumRepository.getInstance().findAlbumById(albumId);
        ArrayList<Photo> res = new ArrayList<>();
        for(var photoId : album.getPhotoIds()){
            res.add(PhotoRepository.getInstance().findPhotoById(photoId));
        }
        return res;

    }




}
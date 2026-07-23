package Services;

import Exceptions.AccessDeniedException;
import Exceptions.ActionFailedException;
import Exceptions.ItemDoesNotExistException;
import Exceptions.ItemNotFoundException;
import MainClasses.Album;
import MainClasses.Photo;
import MainClasses.User;
import Repositories.AlbumRepository;
import Repositories.PhotoRepository;
import Repositories.SessionRepository;
import Repositories.UserRepository;

import java.util.ArrayList;

public class PhotoAlbumService{

    private void validateAccess(Photo photo , Album album){
        User photoOwner = UserRepository.getInstance().findUserById(photo.getOwnerId());
        User albumOwner = UserRepository.getInstance().findUserById(album.getOwnerId());
        if(!photoOwner.equals(albumOwner)){
            throw new AccessDeniedException("Access Denied!!!");
        }
    }

    public void addPhotoToAlbum(String sessionId , String photoId , String albumId){
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        Photo photo = PhotoRepository.getInstance().findPhotoById(photoId , user.getId());
        if(albumId.isEmpty()){
            photo.getPhotoAlbumIds().add(albumId);
        }
        else {
            Album album = AlbumRepository.getInstance().findAlbumById(albumId , user.getId());
            validateAccess(photo, album);
            photo.getPhotoAlbumIds().add(albumId);
            album.getPhotoIds().add(photoId);
            album.updateTime();
        }
    }

    public void removePhotoFromAlbum(String sessionId , String photoId , String albumId){
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        Photo photo = PhotoRepository.getInstance().findPhotoById(photoId , user.getId());
        if(albumId.isEmpty()){
            photo.getPhotoAlbumIds().remove(albumId);
        }
        else {
            Album album = AlbumRepository.getInstance().findAlbumById(albumId , user.getId());
            validateAccess(photo , album);
            photo.getPhotoAlbumIds().remove(albumId);
            album.getPhotoIds().remove(photoId);
            album.updateTime();
        }
    }

    public void validateToRemove(String photoId , Album album){
        if(!album.getPhotoIds().contains(photoId)){
            throw new ActionFailedException("Photo was not found!!!");
        }
    }

    private void validateToRemoveFromNull(Photo photo){
        if(!photo.getPhotoAlbumIds().contains("")){
            throw new ActionFailedException("Photo was not sound");
        }
    }

    public void transferPhoto(String sessionId , String photoId , String fromAlbumId , String toAlbumId){
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        Photo photo = PhotoRepository.getInstance().findPhotoById(photoId , user.getId());
        if(!fromAlbumId.isEmpty() && !toAlbumId.isEmpty()){
            Album fromAlbum = AlbumRepository.getInstance().findAlbumById(fromAlbumId , user.getId());
            Album toAlbum = AlbumRepository.getInstance().findAlbumById(toAlbumId , user.getId());
            validateAccess(photo , fromAlbum);
            validateAccess(photo , toAlbum);
            validateToRemove(photoId , fromAlbum);
            fromAlbum.getPhotoIds().remove(photoId);
            fromAlbum.updateTime();
            toAlbum.getPhotoIds().add(photoId);
            toAlbum.updateTime();
        } else if (!fromAlbumId.isEmpty()){
            Album fromAlbum = AlbumRepository.getInstance().findAlbumById(fromAlbumId , user.getId());
            validateAccess(photo , fromAlbum);
            validateToRemove(photoId , fromAlbum);
            fromAlbum.getPhotoIds().remove(photoId);
            fromAlbum.updateTime();
        } else if (!toAlbumId.isEmpty()){
            validateToRemoveFromNull(photo);
            Album toAlbum = AlbumRepository.getInstance().findAlbumById(toAlbumId , user.getId());
            validateAccess(photo , toAlbum);
            toAlbum.getPhotoIds().add(photoId);
            toAlbum.updateTime();
        } else {
            validateToRemoveFromNull(photo);
        }
        photo.getPhotoAlbumIds().remove(fromAlbumId);
        photo.getPhotoAlbumIds().add(toAlbumId);
    }

    public ArrayList<Photo> getPhotosByAlbumId(String sessionId , String albumId){
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        Album album = AlbumRepository.getInstance().findAlbumById(albumId , user.getId());
        ArrayList<Photo> res = new ArrayList<>();
        for(var photoId : album.getPhotoIds()){
            res.add(PhotoRepository.getInstance().findPhotoById(photoId , user.getId()));
        }
        return res;

    }
}
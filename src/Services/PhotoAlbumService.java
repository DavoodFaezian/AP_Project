package Services;

import Exceptions.AccessDeniedException;
import Exceptions.ItemDoesNotExistException;
import Exceptions.ItemNotFoundException;
import MainClasses.Album;
import MainClasses.Photo;
import MainClasses.User;
import Repositories.AlbumRepository;
import Repositories.PhotoRepository;
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

    public void addPhotoToAlbum(String photoId , String albumId){
        Photo photo = PhotoRepository.getInstance().findPhotoById(photoId);
        if(albumId.isEmpty()){
            photo.getPhotoAlbumIds().add(albumId);
        }
        else {
            Album album = AlbumRepository.getInstance().findAlbumById(albumId);
            validateAccess(photo, album);
            photo.getPhotoAlbumIds().add(albumId);
            album.getPhotoIds().add(photoId);
            album.updateTime();
        }
    }

    public void removePhotoFromAlbum(String userId , String photoId , String albumId){
        Photo photo = PhotoRepository.getInstance().findPhotoById(photoId);
        if(albumId.isEmpty()){
            photo.getPhotoAlbumIds().remove(albumId);
        }
        else {
            Album album = AlbumRepository.getInstance().findAlbumById(albumId);
            validateAccess(photo , album);
            photo.getPhotoAlbumIds().remove(albumId);
            album.getPhotoIds().remove(photoId);
            album.updateTime();
        }
    }

    public void validateToRemove(String photoId , String albumId){
        Album album = AlbumRepository.getInstance().findAlbumById(albumId);
        if(!album.getPhotoIds().contains(photoId)){
            throw new ItemNotFoundException("photo" , photoId);
        }
    }

    private void validateToRemoveFromNull(Photo photo){
        if(!photo.getPhotoAlbumIds().contains("")){
            throw new ItemNotFoundException("photo" , photo.getId());
        }
    }

    public void transferPhoto(String photoId , String fromAlbumId , String toAlbumId){
        Photo photo = PhotoRepository.getInstance().findPhotoById(photoId);
        if(!fromAlbumId.isEmpty() && !toAlbumId.isEmpty()){
            Album fromAlbum = AlbumRepository.getInstance().findAlbumById(fromAlbumId);
            Album toAlbum = AlbumRepository.getInstance().findAlbumById(toAlbumId);
            validateAccess(photo , fromAlbum);
            validateAccess(photo , toAlbum);
            validateToRemove(photoId , fromAlbumId);
            fromAlbum.getPhotoIds().remove(photoId);
            fromAlbum.updateTime();
            toAlbum.getPhotoIds().add(photoId);
            toAlbum.updateTime();
        } else if (!fromAlbumId.isEmpty()){
            Album fromAlbum = AlbumRepository.getInstance().findAlbumById(fromAlbumId);
            validateAccess(photo , fromAlbum);
            validateToRemove(photoId , fromAlbumId);
            fromAlbum.getPhotoIds().remove(photoId);
            fromAlbum.updateTime();
        } else if (!toAlbumId.isEmpty()){
            validateToRemoveFromNull(photo);
            Album toAlbum = AlbumRepository.getInstance().findAlbumById(toAlbumId);
            validateAccess(photo , toAlbum);
            toAlbum.getPhotoIds().add(photoId);
            toAlbum.updateTime();
        } else {
            validateToRemoveFromNull(photo);
        }
        photo.getPhotoAlbumIds().remove(fromAlbumId);
        photo.getPhotoAlbumIds().add(toAlbumId);
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
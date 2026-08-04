package Services;

import Annotaions.ServiceAction;
import DTO.Photo.*;
import Exceptions.ActionFailedException;
import MainClasses.Album;
import MainClasses.Photo;
import MainClasses.User;
import Repositories.AlbumRepository;
import Repositories.PhotoRepository;
import Repositories.SessionRepository;


import java.util.ArrayList;
import java.util.stream.Collectors;

public class PhotoAlbumService{

    private static final PhotoAlbumService instance = new PhotoAlbumService();

    private PhotoAlbumService(){}

    @ServiceAction
    public static PhotoAlbumService getInstance() {
        return instance;
    }

    @ServiceAction
    private void validatePhoto(String photoId) {
        if (photoId == null) {
            throw new ActionFailedException("Photo must not be null");
        }
    }

    @ServiceAction
    public void addPhotoToAlbum(AddPhotoToAndRemovePhotoFromAlbum data) {
        String sessionId = data.getSessionId();
        String photoId = data.getPhotoId();
        String albumId = data.getAlbumId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        validatePhoto(photoId);
        Photo photo = PhotoRepository.getInstance().findPhotoById(photoId , user.getId());
        addMethod(albumId, photo, user, photoId);
    }

    private static void addMethod(String albumId, Photo photo, User user, String photoId) {
        if(albumId.isEmpty()){
            photo.getAlbumIds().add(albumId);
        }
        else {
            Album album = AlbumRepository.getInstance().findAlbumById(albumId, user.getId());
            photo.getAlbumIds().add(albumId);
            album.getPhotoIds().add(photoId);
            album.updateTime();
            AlbumRepository.getInstance().update(album);
        }
        PhotoRepository.getInstance().update(photo);
    }

    @ServiceAction
    public void removePhotoFromAlbum(AddPhotoToAndRemovePhotoFromAlbum data){
        String sessionId = data.getSessionId();
        String photoId = data.getPhotoId();
        String albumId = data.getAlbumId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        Photo photo = PhotoRepository.getInstance().findPhotoById(photoId , user.getId());
        removeMethod(albumId, photo, user, photoId);
    }

    private static void removeMethod(String albumId, Photo photo, User user, String photoId) {
        if(albumId.isEmpty()){
            photo.getAlbumIds().remove(albumId);
        }
        else {
            Album album = AlbumRepository.getInstance().findAlbumById(albumId, user.getId());
            photo.getAlbumIds().remove(albumId);
            album.getPhotoIds().remove(photoId);
            album.updateTime();
            AlbumRepository.getInstance().update(album);
        }
        PhotoRepository.getInstance().update(photo);
    }

    private void validateToRemove(String photoId , Album album){
        if(!album.getPhotoIds().contains(photoId)){
            throw new ActionFailedException("Photo was not found.");
        }
    }

    private void validateToRemoveFromNull(Photo photo){
        if(!photo.getAlbumIds().contains("")){
            throw new ActionFailedException("Photo was not found.");
        }
    }

    @ServiceAction
    public void movePhoto(MovePhotoDto data) {
        String sessionId = data.getSessionId();
        String photoId = data.getPhotoId();
        String fromAlbumId = data.getFromAlbumId();
        String toAlbumId = data.getToAlbumId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        Photo photo = PhotoRepository.getInstance().findPhotoById(photoId , user.getId());
        if(!fromAlbumId.isEmpty() && !toAlbumId.isEmpty()){
            Album fromAlbum = AlbumRepository.getInstance().findAlbumById(fromAlbumId , user.getId());
            Album toAlbum = AlbumRepository.getInstance().findAlbumById(toAlbumId , user.getId());
            validateToRemove(photoId , fromAlbum);
            fromAlbum.getPhotoIds().remove(photoId);
            fromAlbum.updateTime();
            toAlbum.getPhotoIds().add(photoId);
            toAlbum.updateTime();
            AlbumRepository.getInstance().update(fromAlbum);
            AlbumRepository.getInstance().update(toAlbum);
        } else if (!fromAlbumId.isEmpty()){
            Album fromAlbum = AlbumRepository.getInstance().findAlbumById(fromAlbumId , user.getId());
            validateToRemove(photoId , fromAlbum);
            fromAlbum.getPhotoIds().remove(photoId);
            fromAlbum.updateTime();
            AlbumRepository.getInstance().update(fromAlbum);
        } else if (!toAlbumId.isEmpty()){
            validateToRemoveFromNull(photo);
            Album toAlbum = AlbumRepository.getInstance().findAlbumById(toAlbumId , user.getId());
            toAlbum.getPhotoIds().add(photoId);
            toAlbum.updateTime();
            AlbumRepository.getInstance().update(toAlbum);
        } else {
            validateToRemoveFromNull(photo);
        }
        photo.getAlbumIds().remove(fromAlbumId);
        photo.getAlbumIds().add(toAlbumId);
        PhotoRepository.getInstance().update(photo);
    }

    @ServiceAction
    public void editPhotoByAlbum(EditPhotoByAlbumDto data) {
        String sessionId = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        Photo photo = PhotoRepository.getInstance().findPhotoById(data.getPhotoId() , user.getId());

        for (String i : data.getAlbumIds()) {
            if (!photo.getAlbumIds().contains(i)) {
                addMethod(i , photo , user , photo.getId());
            }
        }

        for (String i : photo.getAlbumIds()) {
            if (!data.getAlbumIds().contains(i)) {
               removeMethod(i , photo , user , photo.getId());
            }
        }

    }

    @ServiceAction
    public PhotoListDto getPhotosByAlbumId(GetAlbumPhotosDto data){
        User user = SessionRepository.getInstance().findUserBySessionId(data.getSessionId());
        Album album = AlbumRepository.getInstance().findAlbumById(data.getAlbumId() , user.getId());
        ArrayList<Photo> res = new ArrayList<>();
        for(var photoId : album.getPhotoIds()){
            res.add(PhotoRepository.getInstance().findPhotoById(photoId , user.getId()));
        }
        return new PhotoListDto(res.stream().map(PhotoDto::new).collect(Collectors.toList()));

    }
}
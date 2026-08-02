package Services;
import DTO.Photo.*;
import Exceptions.ActionFailedException;
import Exceptions.ItemNotFoundException;
import MainClasses.Album;
import MainClasses.Photo;
import MainClasses.User;
import Repositories.AlbumRepository;
import Repositories.PhotoRepository;
import Repositories.SessionRepository;
import Repositories.UserRepository;

import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PhotoService {

    private final static PhotoService instance = new PhotoService();

    private PhotoService(){}

    public static PhotoService getInstance() {
        return instance;
    }

    public void validatePhotoName(String photoName) {
        if (photoName.isEmpty()) {
           throw new ActionFailedException("Photo name must not be empty.");
        }
    }

    public String addPhoto(AddPhotoDto data) {
        String sessionId = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        String photoName = data.getName();
        String albumId = data.getAlbumId();
        Set<String> tags = data.getTags();
        String caption = data.getCaption();
        Boolean isFavorable = data.getFavorable();
        String photoData = data.getPhotoData();
        validatePhotoName(photoName);
        byte[] photoBytes = uploadPhoto(photoData);
        Photo photo = PhotoRepository.getInstance().createPhoto(user.getId() , photoName , albumId , tags , caption , isFavorable, photoBytes);
        if (!albumId.isEmpty()) {
            Album album = AlbumRepository.getInstance().findAlbumById(albumId , user.getId());
            album.getPhotoIds().add(photo.getId());
            AlbumRepository.getInstance().update(album);
        }
        return photo.getId();
    }

    public void deletePhoto(DeletePhotoDto data) {
        String sessionId = data.getSessionId();
        String photoId = data.getPhotoId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        Photo photo = PhotoRepository.getInstance().findPhotoById(photoId , user.getId());
        for (String i : photo.getAlbumIds()) {
            if (!i.isEmpty()) {
                Album album = AlbumRepository.getInstance().findAlbumById(i , user.getId());
                album.getPhotoIds().remove(photoId);
                album.updateTime();
                AlbumRepository.getInstance().update(album);
            }
        }
        PhotoRepository.getInstance().removePhoto(photo);

    }

    public void editPhoto(EditPhotoDto data) {
        String sessionId = data.getSessionId();
        SessionRepository.getInstance().validateSession(sessionId);
        Photo photo = data.getPhoto();
        PhotoRepository.getInstance().editPhoto(photo);
    }


    public byte[] uploadPhoto(String photoData) {
        return Base64.getDecoder().decode(photoData);
    }
    public PhotoDto getPhotoById(GetPhotoDto data){
        String sessionId = data.getSessionId();
        SessionRepository.getInstance().validateSession(sessionId);
        var res = PhotoRepository.getInstance().getPhotoById(data.getOwnerId(), data.getPhotoId());
        if(res.isEmpty())
        {
            throw new ItemNotFoundException("Photo", data.getPhotoId());
        }
        return new PhotoDto(res.get());

    }
    public byte[] getPhotoBytes(GetPhotoDto data){
        String sessionId = data.getSessionId();
        SessionRepository.getInstance().validateSession(sessionId);
        return PhotoRepository.getInstance().getPhotoBytes(data.getOwnerId(), data.getPhotoId());


    }

    public List<PhotoDto> getPhotosByOwnerId(GetAllPhotosDto data){
        String sessionId = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        return PhotoRepository.getInstance().getPhotosByOwnerId(user.getId())
                .stream()
                .map(PhotoDto::new)
                .collect(Collectors.toList());


    }
}

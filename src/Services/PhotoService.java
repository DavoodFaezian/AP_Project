package Services;
import DTO.Photo.AddPhotoDto;
import DTO.Photo.DeletePhotoDto;
import Exceptions.ActionFailedException;
import MainClasses.Album;
import MainClasses.Photo;
import MainClasses.User;
import Repositories.AlbumRepository;
import Repositories.PhotoRepository;
import Repositories.SessionRepository;
import Repositories.UserRepository;

import java.util.Set;

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
        validatePhotoName(photoName);
        Photo photo = PhotoRepository.getInstance().createPhoto(user.getId() , photoName , albumId , tags , caption , isFavorable);
        if (!albumId.isEmpty()) {
            Album album = AlbumRepository.getInstance().findAlbumById(albumId , user.getId());
            album.getPhotoIds().add(photo.getId());
            AlbumRepository.getInstance().update(album);
        }
        user.getPhotoIds().add(photo.getId());
        UserRepository.getInstance().update();
        return photo.getId();
    }

    public void deletePhoto(DeletePhotoDto data) {
        String sessionId = data.getSessionId();
        String photoId = data.getPhotoId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        Photo photo = PhotoRepository.getInstance().findPhotoById(photoId , user.getId());
        user.getPhotoIds().remove(photoId);
        PhotoRepository.getInstance().removePhoto(photo);
        UserRepository.getInstance().update();
    }

}

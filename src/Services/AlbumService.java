package Services;

import Dto.AddAlbumDto;
import Dto.DeleteAlbumDto;
import Exceptions.AccessDeniedException;
import MainClasses.Album;
import MainClasses.Photo;
import MainClasses.User;
import Repositories.AlbumRepository;
import Repositories.PhotoRepository;
import Repositories.SessionRepository;
import Repositories.UserRepository;

public class AlbumService {

    private static final AlbumService instance = new AlbumService();

    private AlbumService(){
    }

    public static AlbumService getInstance() {
        return instance;
    }

    public void addAlbum(AddAlbumDto data) {
        String sessionId = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        String albumName = data.getName();
        AlbumRepository.getInstance().createAlbum(user.getId() , albumName);
    }

    public void deleteAlbum(DeleteAlbumDto data) {
        String sessionId = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        String albumId = data.getAlbumId();
        Album album = AlbumRepository.getInstance().findAlbumById(albumId , user.getId());
        for(String i : album.getPhotoIds()) {
            Photo photo = PhotoRepository.getInstance().findPhotoById(i , user.getId());
            photo.getAlbumIds().remove(album.getId());
            if (photo.getAlbumIds().isEmpty()) {
                PhotoRepository.getInstance().removePhoto(photo);
            } else {
                PhotoRepository.getInstance().update(photo);
            }
        }
        user.getAlbumIds().remove(album.getId());
        AlbumRepository.getInstance().removeAlbum(album);
        UserRepository.getInstance().update();
    }
}

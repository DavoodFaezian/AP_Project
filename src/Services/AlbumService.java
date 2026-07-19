package Services;

import Exceptions.AccessDeniedException;
import MainClasses.Album;
import MainClasses.User;
import Repositories.AlbumRepository;
import Repositories.UserRepository;

public class AlbumService {

    private void validateAccess(User user , Album album){
        if(!album.getOwnerId().equals(user.getId())){
            throw new AccessDeniedException("Access Denied!!!");
        }
    }

    public void matchUserWithAlbum(String userId , String albumId){
        User user = UserRepository.getInstance().findUserById(userId);
        Album album = AlbumRepository.getInstance().findAlbumById(albumId,userId);
        validateAccess(user , album);
        user.getAlbumIds().add(albumId);
    }
}

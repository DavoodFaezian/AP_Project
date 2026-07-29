package Services;

import Dto.AddAlbumDto;
import Exceptions.AccessDeniedException;
import MainClasses.Album;
import MainClasses.User;
import Repositories.AlbumRepository;
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
}

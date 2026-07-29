package Services;

import Dto.AddPhotoDto;
import Exceptions.AccessDeniedException;
import FileManager.GenericFileManager;
import MainClasses.Photo;
import MainClasses.Session;
import MainClasses.User;
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

    public void addPhoto(AddPhotoDto data) {
        String sessionId = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        String photoName = data.getName();
        Set<String> tags = data.getTags();
        String caption = data.getCaption();
        Boolean isFavorable = data.getFavorable();
        Boolean permissionForLeavingComment = data.getPermissionForLeavingComment();
        PhotoRepository.getInstance().createPhoto(user.getId() , photoName , tags , caption , isFavorable , permissionForLeavingComment);
    }
}

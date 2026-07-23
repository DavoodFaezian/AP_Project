package Services;

import Exceptions.AccessDeniedException;
import FileManager.GenericFileManager;
import MainClasses.Photo;
import MainClasses.User;
import Repositories.PhotoRepository;
import Repositories.UserRepository;

public class PhotoService {

    private final static PhotoService instance = new PhotoService();

    private PhotoService(){}

    public static PhotoService getInstance() {
        return instance;
    }

    private void validateAccess(User user , Photo photo){
        if(!photo.getOwnerId().equals(user.getId())){
            throw new AccessDeniedException("Access Denied!!!");
        }
    }

    public void matchPhotoWithUser(String userId , String photoId){
        User user = UserRepository.getInstance().findUserById(userId);
        Photo photo = PhotoRepository.getInstance().findPhotoById(photoId,userId);
        validateAccess(user , photo);
        user.getPhotoIds().add(photoId);
    }
}

package Services;

import Exceptions.AccessDeniedException;
import MainClasses.Photo;
import MainClasses.User;
import Repositories.PhotoRepository;
import Repositories.UserRepository;

public class PhotoService {

    private void validateAccess(User user , Photo photo){
        if(!photo.getOwnerId().equals(user.getId())){
            throw new AccessDeniedException("Access Denied!!!");
        }
    }

    public void matchPhotoWithUser(String userId , String photoId){
        User user = UserRepository.getInstance().findUserById(userId);
        Photo photo = PhotoRepository.getInstance().findPhotoById(photoId);
        validateAccess(user , photo);
        user.getPhotoIds().add(photoId);
    }
}

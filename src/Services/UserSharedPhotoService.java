package Services;

import Exceptions.AccessDeniedException;
import MainClasses.Album;
import MainClasses.Photo;
import MainClasses.User;
import Repositories.PhotoRepository;
import Repositories.UserRepository;

import java.util.ArrayList;

public class UserSharedPhotoService {
    private void validateUser(User user){
        if(user == null){
            throw new NullPointerException("User is null!!!");
        }
    }

    private void validateAccess(Photo photo , User user){
        if(!photo.getOwner().equals(user)){
            throw new AccessDeniedException("Access Denied!!!");
        }
    }

    private void validatePhoto(Photo photo){
        if(photo == null){
            throw new NullPointerException("Photo is null!!!");
        }
    }
    public void sharePhoto(Photo photo , User sender , User receiver){
        validateUser(sender);
        validateUser(receiver);
        validatePhoto(photo);
        validateAccess(photo , sender);
        receiver.getSharedPhotos().add(photo);
        photo.getSharedWithUsers().add(receiver);
        photo.updateDateOfShare();
    }

    public void undoSharePhoto(Photo photo , User sender , User receiver){
        validateUser(sender);
        validateUser(receiver);
        validatePhoto(photo);
        validateAccess(photo , sender);
        receiver.getSharedPhotos().remove(photo);
        photo.getSharedWithUsers().remove(receiver);
    }

    public ArrayList<Photo> getPhotosByUserId(String userId){
        User user = UserRepository.getInstance().findUserById(userId);
        ArrayList<Photo> res = new ArrayList<>();
        for(var photoId : user.getPhotoIds()){
            res.add(PhotoRepository.getInstance().findPhotoById(photoId));
        }
        return res;

    }
}

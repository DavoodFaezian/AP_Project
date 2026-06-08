package Services;

import Exceptions.AccessDeniedException;
import Exceptions.ItemDoesNotExistException;
import MainClasses.Photo;
import MainClasses.User;
import Repositories.PhotoRepository;
import Repositories.UserRepository;

import java.util.ArrayList;

public class UserSharedPhotoService {

    private void validateAccess(String photoId , String userId){
        if(!PhotoRepository.getInstance().findPhotoById(photoId).getOwnerId().equals(userId)){
            throw new AccessDeniedException("Access Denied!!!");
        }
    }
    private void validateSharePhotoInputs(String photoId, String senderId, String receiverId){
        if(!(UserRepository.getInstance().isUserIdValid(senderId)&&
                UserRepository.getInstance().isUserIdValid(receiverId)&&
                PhotoRepository.getInstance().isPhotoIdValid(photoId))){
            throw new ItemDoesNotExistException("Photo, sender or receiver doesn't exist");

        }
    }

    public void sharePhoto(String photoId, String senderId, String receiverId){
        Photo photo = PhotoRepository.getInstance().findPhotoById(photoId);
        User receiver = UserRepository.getInstance().findUserById(receiverId);
        validateSharePhotoInputs(photoId,senderId,receiverId);
        validateAccess(photoId, senderId);
        receiver.getSharedPhotoIds().add(photoId);
        photo.getSharedUserIds().add(receiverId);
        photo.updateDateOfShare();

    }

    public void undoSharePhoto(String photoId, String senderId, String receiverId){
        Photo photo = PhotoRepository.getInstance().findPhotoById(photoId);
        User receiver = UserRepository.getInstance().findUserById(receiverId);
        validateSharePhotoInputs(photoId,senderId,receiverId);
        validateAccess(photoId , senderId);
        receiver.getSharedPhotoIds().remove(photoId);
        photo.removeSharedUser(receiverId);
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

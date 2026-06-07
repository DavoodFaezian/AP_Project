package Services;

import Exceptions.AccessDeniedException;
import MainClasses.Photo;
import MainClasses.User;
import Repositories.PhotoRepository;
import Repositories.UserRepository;

public class UserSharedPhotoService{

    private void validateAccess(String photoId , String senderId){
        Photo photo = PhotoRepository.getInstance().findPhotoById(photoId);
        if(!photo.getOwnerId().equals(senderId)){
            throw new AccessDeniedException("Access denied!!!");
        }
    }

    private void validate(String item){
        if(item.isEmpty()){
            throw new NullPointerException("Parameter is null!!!");
        }
    }

    public void sharePhoto(String photoId , String senderId , String receiverId){
        validate(photoId);
        validate(senderId);
        validate(receiverId);
        validateAccess(photoId , senderId);
        User receiver = UserRepository.getInstance().findUserById(receiverId);
        receiver.getSharedPhotoIds().add(photoId);
        Photo photo = PhotoRepository.getInstance().findPhotoById(photoId);
        photo.getSharedUserIds().add(receiverId);
        photo.updateDateOfShare();
    }

    public void undoSharePhoto(String photoId , String senderId , String receiverId){
        validate(photoId);
        validate(senderId);
        validate(receiverId);
        User receiver = UserRepository.getInstance().findUserById(receiverId);
        receiver.getSharedPhotoIds().remove(photoId);
        Photo photo = PhotoRepository.getInstance().findPhotoById(photoId);
        photo.getSharedUserIds().remove(receiverId);
        photo.updateDateOfShare();
    }
}
package Repositories;

import FileManager.GenericFileManager;
import MainClasses.Photo;
import MainClasses.PhotoSharedUser;
import MainClasses.User;

import java.util.List;
import java.util.stream.Collectors;

public class PhotoSharedUserRepository {

    private final GenericFileManager<PhotoSharedUser> shareFileManager;
    private static final PhotoSharedUserRepository instance = new PhotoSharedUserRepository();

    private PhotoSharedUserRepository() {
        this.shareFileManager = new GenericFileManager<>("shared_photos.txt");
    }

    public static PhotoSharedUserRepository getInstance() {
        return instance;
    }

    public void addShareUserPhoto(String photoId, String userId) {
        PhotoSharedUser sharedEntry = new PhotoSharedUser(photoId, userId);
        shareFileManager.addToList(sharedEntry);
        shareFileManager.save();
    }

    public void removeShareUserPhoto(String photoId, String userId) {
        shareFileManager.removeFromListIf(s -> s.getPhotoId().equals(photoId) && s.getSharedUserId().equals(userId));
        shareFileManager.save();
    }

    public List<Photo> getPhotosWithUserId(String userId) {
        return shareFileManager.filterItems(s -> s.getSharedUserId().equals(userId))
                .stream()
                .map(PhotoSharedUser::getPhoto)
                .collect(Collectors.toList());
    }

    public List<User> getUsersWithAccessToPhoto(String photoId) {
        return shareFileManager.filterItems(s -> s.getPhotoId().equals(photoId))
                .stream()
                .map(PhotoSharedUser::getSharedUser)
                .collect(Collectors.toList());
    }

}

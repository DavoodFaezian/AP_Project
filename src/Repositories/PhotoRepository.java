package Repositories;

import Exceptions.ItemNotFoundException;
import MainClasses.Photo;

import java.util.List;
import java.util.Optional;

public class PhotoRepository extends BaseRepository<Photo>{
    private static PhotoRepository instance = new PhotoRepository();

    private PhotoRepository() {
        super("photos");
    }

    public static PhotoRepository getInstance() {
        return instance;
    }

    public void addPhoto(Photo photo) {
        var photoFileManager = getFileManager(photo.getOwnerId());
        photoFileManager.addToList(photo);
    }

    public void removePhoto(Photo photo) {
        var photoFileManager = getFileManager(photo.getOwnerId());
        photoFileManager.removeFromList(photo);

    }

    public void removePhoto(String id,String ownerId) {
        Photo remove = findPhotoById(id,ownerId);
        removePhoto(remove);
    }
    public void editPhoto(Photo edit){
        var photoFileManager = getFileManager(edit.getOwnerId());
        photoFileManager.edit(edit);
    }
    public List<Photo> getPhotosByOwnerId(String ownerId){
        var photoFileManager = getFileManager(ownerId);
        return photoFileManager.getAll();
    }



    public Photo findPhotoById(String id,String ownerId){
        Optional<Photo> photo = getFileManager(ownerId).findItemById(id);
        if(photo.isEmpty()){
            throw new ItemNotFoundException("photo", id);
        }
        return photo.get();
    }


    public boolean isPhotoIdValid(String photoId,String ownerId){
        return getFileManager(ownerId).exists(p->p.getId().equals(photoId));
    }
}

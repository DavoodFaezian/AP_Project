package Repositories;

import Exceptions.ItemNotFoundException;
import FileManager.GenericFileManager;
import MainClasses.Photo;

import java.util.List;
import java.util.Optional;

public class PhotoRepository {

    private final GenericFileManager<Photo> photoFileManager;
    private static final PhotoRepository instance = new PhotoRepository();
    private PhotoRepository() {
        this.photoFileManager = new GenericFileManager<>("photo.txt");
    }
    public static PhotoRepository getInstance() {
        return instance;
    }

    public void addPhoto(Photo photo) {
        photoFileManager.addToList(photo);
        photoFileManager.save();
    }

    public void removePhoto(Photo photo) {
        photoFileManager.removeFromList(photo);
        photoFileManager.save();
    }

    public void removePhoto(String id) {
        Photo remove = findPhotoById(id);
        removePhoto(remove);
    }



    public Photo findPhotoById(String id){
        Optional<Photo> photo = photoFileManager.findItemById(id);
        if(photo.isEmpty()){
            throw new ItemNotFoundException("photo" , id);
        }
        return photo.get();
    }


    public List<Photo> findPhotoByOwner(String ownerId){
        return photoFileManager.filterItems(p -> p.getOwnerId().equals(ownerId));
    }
    public void save(){
        photoFileManager.save();
    }
}

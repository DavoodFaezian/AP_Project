package Repositories;

import Exceptions.ItemNotFoundException;
import FileManager.GenericFileManager;
import MainClasses.Photo;

import java.util.List;
import java.util.Optional;

public class PhotoRepository {

    private final GenericFileManager<Photo> photoFileManager;

    private PhotoRepository(){
        this.photoFileManager = new GenericFileManager<Photo>(Photo.fileName);
    }

    public static PhotoRepository getInstance(){
        return new PhotoRepository();
    }

    public void savePhoto(Photo photo){
        for(int i = 0 ; i < photoFileManager.getAll().size() ; i++){
            if(photo.equals(photoFileManager.getAll().get(i))){
                photoFileManager.getAll().set(i , photo);
                break;
            }
        }
    }

    public Photo findPhotoById(String id){
        Optional<Photo> photo = photoFileManager.findItemById(id);
        if(photo.isEmpty()){
            throw new ItemNotFoundException("photo" , id);
        }
        return photo.get();
    }

    public List<Photo> findPhotoByOwner(String ownerId){
        return photoFileManager.filterItems(p -> p.getOwner().getId().equals(ownerId));
    }

}

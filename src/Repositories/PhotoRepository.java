package Repositories;

import Exceptions.ItemNotFoundException;
import FileManager.GenericFileManager;
import MainClasses.Photo;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PhotoRepository {
    private final ConcurrentHashMap<String, ReentrantReadWriteLock> locks = new ConcurrentHashMap<>();
    private static PhotoRepository instance = new PhotoRepository();
    private GenericFileManager<Photo> getPhotoFileManager(String key){
        ReentrantReadWriteLock lock = locks.computeIfAbsent(key,(l)->new ReentrantReadWriteLock());
       return new GenericFileManager<>("photos"+
               File.separator
               +key
               +".txt",lock);
    }
    
    private PhotoRepository() {
    }

    public static PhotoRepository getInstance() {
        return instance;
    }

    public void addPhoto(Photo photo) {
        var photoFileManager = getPhotoFileManager(photo.getOwnerId());
        photoFileManager.addToList(photo);
    }

    public void removePhoto(Photo photo) {
        var photoFileManager = getPhotoFileManager(photo.getOwnerId());
        photoFileManager.removeFromList(photo);

    }

    public void removePhoto(String id,String ownerId) {
        Photo remove = findPhotoById(id,ownerId);
        removePhoto(remove);
    }
    public void editPhoto(Photo edit){
        var photoFileManager = getPhotoFileManager(edit.getOwnerId());
        photoFileManager.edit(edit);
    }
    public List<Photo> getPhotosByOwnerId(String ownerId){
        var photoFileManager = getPhotoFileManager(ownerId);
        return photoFileManager.getAll();
    }



    public Photo findPhotoById(String id,String ownerId){
        Optional<Photo> photo = getPhotoFileManager(ownerId).findItemById(id);
        if(photo.isEmpty()){
            throw new ItemNotFoundException("photo", id);
        }
        return photo.get();
    }


    public boolean isPhotoIdValid(String photoId,String ownerId){
        return getPhotoFileManager(ownerId).exists(p->p.getId().equals(photoId));
    }
}

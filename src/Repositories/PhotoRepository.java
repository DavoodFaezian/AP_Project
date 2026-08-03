package Repositories;

import Exceptions.ActionFailedException;
import Exceptions.ItemNotFoundException;
import FileManager.FileServer;
import MainClasses.Photo;

import java.io.IOException;
import java.util.*;

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
        photoFileManager.save();
    }

    public void removePhoto(Photo photo) {
        var photoFileManager = getFileManager(photo.getOwnerId());
        photoFileManager.removeFromList(photo);
        photoFileManager.save();

        try {
            FileServer.deletePhoto(photo.getOwnerId(),photo.getPhotoName());
        } catch (IOException e) {
            throw new ActionFailedException("Couldn't delete photo.");
        }
    }

    public void update(Photo photo) {
        var photoFileManager = getFileManager(photo.getOwnerId());
        photoFileManager.edit(photo);
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

    public Photo createPhoto(String ownerId,String title, String photoName, String albumId , Set<String> tags, String caption, Boolean isFavorable) {
        Photo photo = new Photo(ownerId , photoName , albumId , tags , caption , isFavorable,title );
        addPhoto(photo);
        return photo;
    }

    public byte[] getPhotoBytes(String ownerId , String photoName) {

        try {
            return FileServer.getPhoto(ownerId,photoName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public Optional<Photo> getPhotoById(String ownerId , String photoId) {
        return getFileManager(ownerId).findItemById(photoId);
    }


    public boolean isPhotoIdValid(String photoId,String ownerId){
        return getFileManager(ownerId).exists(p->p.getId().equals(photoId));
    }
    public String uploadPhoto(String ownerId, byte[] photoBytes){        try {
        return FileServer.savePhoto(ownerId , photoBytes);
        } catch (IOException e) {
            throw new ActionFailedException("Couldn't upload photo.");
        }
    }
}

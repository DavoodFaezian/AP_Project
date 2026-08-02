package Repositories;

import Exceptions.ItemNotFoundException;
import FileManager.FileServer;
import MainClasses.Photo;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PhotoRepository extends BaseRepository<Photo>{
    private static PhotoRepository instance = new PhotoRepository();

    private static final Map<String , FileServer> map = new HashMap<>();

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

        FileServer server = map.get(photo.getOwnerId());
        if (server != null) {
           server.deleteData(photo.getId());
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

    public Photo createPhoto(String ownerId, String photoName, String albumId , Set<String> tags, String caption, Boolean isFavorable, byte[] photoBytes) {
        Photo photo = new Photo(ownerId , photoName , albumId , tags , caption , isFavorable);
        addPhoto(photo);
        String fileName = "user_" + ownerId + ".bin";

        FileServer server = map.computeIfAbsent(ownerId, id ->
                new FileServer(fileName , new ReentrantReadWriteLock()));

        server.saveData(photo.getId() , photoBytes);
        return photo;
    }

    public byte[] getPhotoBytes(String ownerId , String photoId) {
        FileServer server = map.get(ownerId);
        if(server == null) {
            return null;
        }
        return server.readData(photoId);
    }


    public Optional<Photo> getPhotoById(String ownerId , String photoId) {
        return getFileManager(ownerId).findItemById(photoId);
    }


    public boolean isPhotoIdValid(String photoId,String ownerId){
        return getFileManager(ownerId).exists(p->p.getId().equals(photoId));
    }
}

package Repositories;

import FileManager.GenericFileManager;
import MainClasses.Photo;

public class PhotoRepository {

    private final GenericFileManager<Photo> photoFileManager;

    private PhotoRepository(){
        this.photoFileManager = new GenericFileManager<Photo>(Photo.fileName);
    }

    public static PhotoRepository getInstance(){
        return new PhotoRepository();
    }


}

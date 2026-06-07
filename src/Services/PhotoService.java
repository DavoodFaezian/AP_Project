package Services;

import MainClasses.Album;
import MainClasses.Photo;
import MainClasses.User;
import Repositories.AlbumRepository;
import Repositories.PhotoRepository;
import Repositories.UserRepository;
import ViewModels.Photo.CreatePhotoViewModel;
import ViewModels.Photo.EditPhotoViewModel;
import ViewModels.Photo.PhotoViewModel;

import java.util.*;
import java.util.stream.Collectors;

public class PhotoService {
    public void addPhoto(Photo photo){
        PhotoRepository.getInstance().addPhoto(photo);

    }
    public void editPhoto(EditPhotoViewModel editPhoto){
        PhotoRepository.getInstance().save();
    }

    public void removePhoto(String id){
        PhotoRepository.getInstance().removePhoto(id);
    }
    public Photo getPhotoById(String id){
        return PhotoRepository.getInstance().findPhotoById(id);
    }


}

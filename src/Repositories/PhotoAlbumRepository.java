package Repositories;

import Exceptions.ItemNotFoundException;
import FileManager.GenericFileManager;
import MainClasses.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PhotoAlbumRepository {

    private final GenericFileManager<PhotoAlbum> albumFileManager;
    private static final PhotoAlbumRepository instance = new PhotoAlbumRepository();

    private PhotoAlbumRepository() {
        this.albumFileManager = new GenericFileManager<>("photo-albums.txt");
    }

    public static PhotoAlbumRepository getInstance() {
        return instance;
    }

    public void addPhotoAlbum(PhotoAlbum photoAlbum) {
        albumFileManager.addToList(photoAlbum);
        albumFileManager.save();
    }

    public void removePhotoAlbum(String id) {
        PhotoAlbum photoAlbum = findAlbumById(id);
        albumFileManager.removeFromList(photoAlbum);
        albumFileManager.save();
    }
    public void removePhotoAlbum(String photoId, String albumId) {

        albumFileManager.removeFromListIf(p->p.getPhotoId().equals(photoId)
                &&p.getAlbumId().equals(albumId));
        albumFileManager.save();
    }
    public List<Photo> getPhotosByAlbumId(String albumId) {
        return albumFileManager.filterItems(s -> s.getAlbumId().equals(albumId))
                .stream()
                .map(PhotoAlbum::getPhoto)
                .collect(Collectors.toList());
    }

    public List<Album> getAlbumsWithPhoto(String photoId) {
        return albumFileManager.filterItems(s -> s.getPhotoId().equals(photoId))
                .stream()
                .map(PhotoAlbum::getAlbum)
                .collect(Collectors.toList());
    }
    public PhotoAlbum findAlbumById(String id){
        Optional<PhotoAlbum> album = albumFileManager.findItemById(id);

        if(album.isEmpty()){
            throw new ItemNotFoundException("album", id);
        }

        return album.get();
    }

    public List<PhotoAlbum> getAllPhotoAlbums(){
        return albumFileManager.getAll();
    }

    public void save(){
        albumFileManager.save();
    }
}

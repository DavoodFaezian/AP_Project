package Repositories;

import Exceptions.ItemNotFoundException;
import FileManager.GenericFileManager;
import MainClasses.Album;

import java.util.List;
import java.util.Optional;

public class AlbumRepository {

    private final GenericFileManager<Album> albumFileManager;
    private static final AlbumRepository instance = new AlbumRepository();

    private AlbumRepository() {
        this.albumFileManager = new GenericFileManager<>("album.txt");
    }

    public static AlbumRepository getInstance() {
        return instance;
    }

    public void addAlbum(Album album) {
        albumFileManager.addToList(album);
        albumFileManager.save();
    }

    public void removeAlbum(Album album) {
        albumFileManager.removeFromList(album);
        albumFileManager.save();
    }

    public void removeAlbum(String id) {
        Album remove = findAlbumById(id);
        remove.validateRemoveAlbum();

        removeAlbum(remove);
    }

    public Album findAlbumById(String id) {
        Optional<Album> album = albumFileManager.findItemById(id);

        if (album.isEmpty()) {
            throw new ItemNotFoundException("album", id);
        }

        return album.get();
    }

    public List<Album> getAlbumsByOwner(String ownerId) {
        return albumFileManager.filterItems(
                album -> album.getOwnerId().equals(ownerId)
        );
    }

}
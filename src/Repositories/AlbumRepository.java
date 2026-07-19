package Repositories;

import Exceptions.ItemNotFoundException;
import MainClasses.Album;

import java.util.List;
import java.util.Optional;

public class AlbumRepository extends BaseRepository<Album> {
    private static AlbumRepository instance = new AlbumRepository();


    private AlbumRepository() {
        super("albums");
    }

    public static AlbumRepository getInstance() {
        return instance;
    }

    public void addAlbum(Album album) {
        var albumFileManager = getFileManager(album.getOwnerId());
        albumFileManager.addToList(album);
    }

    public void removeAlbum(Album album) {
        var albumFileManager = getFileManager(album.getOwnerId());
        albumFileManager.removeFromList(album);
    }

    public void removeAlbum(String id, String ownerId) {
        Album remove = findAlbumById(id, ownerId);
        remove.validateRemoveAlbum();
        removeAlbum(remove);
    }
    public void editAlbum(Album album){
        var albumFileManager = getFileManager(album.getOwnerId());
        albumFileManager.edit(album);
    }

    public Album findAlbumById(String id,String ownerId) {
        var albumFileManager = getFileManager(ownerId);
        Optional<Album> album = albumFileManager.findItemById(id);

        if (album.isEmpty()) {
            throw new ItemNotFoundException("album", id);
        }

        return album.get();
    }

    public List<Album> getAlbumsByOwner(String ownerId) {
        var albumFileManager = getFileManager(ownerId);
        return albumFileManager.filterItems(
                album -> album.getOwnerId().equals(ownerId)
        );
    }

}
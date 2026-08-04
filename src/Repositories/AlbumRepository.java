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
        albumFileManager.save();
    }

    public void removeAlbum(Album album) {
        var albumFileManager = getFileManager(album.getOwnerId());
        albumFileManager.removeFromList(album);
        albumFileManager.save();
    }

    public void removeAlbum(String id, String ownerId) {
        Album remove = findAlbumById(id, ownerId);
        remove.validateRemoveAlbum();
        removeAlbum(remove);
    }
    public void editAlbum(String albumId,String albumName,String ownerId){
        var albumFileManager = getFileManager(ownerId);
        var edit = albumFileManager.findItemById(albumId);

        if (edit.isEmpty()) {
            throw new ItemNotFoundException("album", albumId);
        }
        edit.get().setAlbumName(albumName);
        albumFileManager.edit(edit.get());
        albumFileManager.save();
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

    public Album createAlbum(String ownerId , String albumName) {
        Album album = new Album(ownerId , albumName);
        addAlbum(album);
        return album;
    }

    public void update(Album album) {
        var albumFileManager = getFileManager(album.getOwnerId());
        albumFileManager.edit(album);
    }

}
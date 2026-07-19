package Repositories;

import Exceptions.ItemNotFoundException;
import FileManager.GenericFileManager;
import MainClasses.Album;
import MainClasses.Photo;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AlbumRepository {
    ConcurrentHashMap<String, ReentrantReadWriteLock> locks = new ConcurrentHashMap<>();
    private static AlbumRepository instance = new AlbumRepository();
    private GenericFileManager<Album> getAlbumFileManager(String key){
        var lock = locks.computeIfAbsent(key,(l)->new ReentrantReadWriteLock());
        return new GenericFileManager<>("albums"
                + File.separator
                +key
                +".txt",lock);
    }

    private AlbumRepository() {
    }

    public static AlbumRepository getInstance() {
        return instance;
    }

    public void addAlbum(Album album) {
        var albumFileManager = getAlbumFileManager(album.getOwnerId());
        albumFileManager.addToList(album);
        albumFileManager.save();
    }

    public void removeAlbum(Album album) {
        var albumFileManager = getAlbumFileManager(album.getOwnerId());
        albumFileManager.removeFromList(album);
        albumFileManager.save();
    }

    public void removeAlbum(String id, String ownerId) {
        Album remove = findAlbumById(id, ownerId);
        remove.validateRemoveAlbum();

        removeAlbum(remove);
    }
    public void editAlbum(Album album){
        var albumFileManager = getAlbumFileManager(album.getOwnerId());
        albumFileManager.edit(album);
    }

    public Album findAlbumById(String id,String ownerId) {
        var albumFileManager = getAlbumFileManager(ownerId);
        Optional<Album> album = albumFileManager.findItemById(id);

        if (album.isEmpty()) {
            throw new ItemNotFoundException("album", id);
        }

        return album.get();
    }

    public List<Album> getAlbumsByOwner(String ownerId) {
        var albumFileManager = getAlbumFileManager(ownerId);
        return albumFileManager.filterItems(
                album -> album.getOwnerId().equals(ownerId)
        );
    }

}
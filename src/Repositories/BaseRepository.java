package Repositories;

import FileManager.GenericFileManager;
import MainClasses.BaseClass;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BaseRepository<T extends BaseClass> {
    private final ConcurrentHashMap<String, ReentrantReadWriteLock> locks = new ConcurrentHashMap<>();
    private final String folderName;

    protected BaseRepository(String folderName) {
        this.folderName = folderName;
    }

    // Common logic for getting a locked file manager
    protected GenericFileManager<T> getFileManager(String key) {
        ReentrantReadWriteLock lock = locks.computeIfAbsent(key, k -> new ReentrantReadWriteLock());

        String path = folderName + File.separator + key + ".txt";
        return new GenericFileManager<>(path, lock);
    }
}

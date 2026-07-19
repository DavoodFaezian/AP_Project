package Repositories;

import FileManager.GenericFileManager;
import MainClasses.BaseClass;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LockedFileManagerFactory<T extends BaseClass> {
    private final ConcurrentHashMap<String, ReentrantReadWriteLock> locks = new ConcurrentHashMap<>();
    private final String folderName;

    public LockedFileManagerFactory(String folderName) {
        this.folderName = folderName;
    }

    public GenericFileManager<T> get(String key) {
        ReentrantReadWriteLock lock =
                locks.computeIfAbsent(key, k -> new ReentrantReadWriteLock());

        return new GenericFileManager<>(
                folderName + File.separator + key + ".txt",
                lock
        );
    }
}


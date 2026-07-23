package FileManager;

import MainClasses.BaseClass;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;

public class GenericFileManager<T extends BaseClass> {
    // Get the path where the server will run.
    private static final Path CURRENT_DIR = Paths.get(System.getProperty("user.dir"));
    private final Map<String,T> map = new HashMap<>();
    private final File filePath;

    private final Lock readLock;
    private final Lock writeLock;

    public GenericFileManager(String fileName,ReentrantReadWriteLock lock) {
        writeLock = lock.readLock();
        readLock = lock.writeLock();
        Path path = Paths.get(CURRENT_DIR.toString(), "files");

        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }


        this.filePath = Paths.get(CURRENT_DIR.toString(),"files",fileName).toFile();
        if(!filePath.exists() || filePath.length() == 0){
            try {
                if (!filePath.exists()) {
                    Files.createFile(filePath.toPath());
                }
                save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        try(ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(
                        new FileInputStream(filePath)))){
            int itemsCount = in.readInt();
            for (int i = 0; i < itemsCount; i++) {
                T item = (T) in.readObject();
                map.put(item.getId(),item);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("An error was caught trying to read the file.");
        }

    }

    public void save() {
        writeLock.lock();
        try {
            saveInternal();
        } finally {
            writeLock.unlock();
        }
    }

    @SafeVarargs
    public final List<T> filterItems(Predicate<T>... conditions) {
        readLock.lock();
        try {
            List<T> result = new ArrayList<>();
            outer:
            for (T item : map.values()) {
                for (Predicate<T> condition : conditions) {
                    if (!condition.test(item)) {
                        continue outer;
                    }
                }
                result.add(item);
            }
            return List.copyOf(result);
        } finally {
            readLock.unlock();
        }
    }


    public Optional<T> findItemById(String id){
        readLock.lock();
        try {
            return Optional.ofNullable(map.get(id));
        }
        finally {
            readLock.unlock();
        }
    }

    public List<T> getAll() {
        readLock.lock();
        try {
            return List.copyOf(map.values());
        }
        finally {
            readLock.unlock();
        }
    }

    @SafeVarargs
    public final boolean exists(Predicate<T>... conditions){
        readLock.lock();
        try {
            Predicate<T> allConditions = Arrays.stream(conditions)
                    .reduce(Predicate::and)
                    .orElse(c -> false);

            return map.values().stream()
                    .anyMatch(allConditions);
        } finally {
            readLock.unlock();
        }
    }
    public boolean edit(T replacement){
        if (replacement == null) {
            return false;
        }
        writeLock.lock();
        try {
            if (map.replace(replacement.getId(), replacement) != null) {
                saveInternal();
                return true;
            }
            return false;
        }
        finally {
            writeLock.unlock();
        }
    }

    public void addToList(T item){
        writeLock.lock();
        try {
            map.put(item.getId(),item);
            saveInternal();
        }
        finally {
            writeLock.unlock();
        }
    }

    public void removeFromList(T item){
        if (item == null) return;
        writeLock.lock();
        try {
            if (map.remove(item.getId()) != null) {
                saveInternal();
            }
        } finally {
            writeLock.unlock();
        }
    }

    public void removeFromListById(String id){
        writeLock.lock();
        try {
            if(!map.containsKey(id)){
                return;
            }
            map.remove(id);
            saveInternal();

        }
        finally {
            writeLock.unlock();
        }
    }

    public void removeFromListIf(Predicate<T> ...conditions) {
        writeLock.lock();
        try{
            var allConditions = Arrays.stream(conditions)
                    .reduce(Predicate::and)
                    .orElse(c->false);
            map.values().removeIf(allConditions);
            saveInternal();
        }
        finally {
            writeLock.unlock();
        }
    }

    public void removeAll(){
        writeLock.lock();
        try {
            map.clear();
            saveInternal();
        }
        finally {
            writeLock.unlock();
        }
    }

    private void saveInternal() {
        try(ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(filePath)))) {
            out.writeInt(map.size());
            for (T item : map.values()) {
                out.writeObject(item);
            }

            out.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save data to disk: " + filePath.getName(), e);
        }
    }
}

package FileManager;

import MainClasses.BaseClass;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GenericFileManager<T extends BaseClass> {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // Get the path where the server will run.
    private static final Path CURRENT_DIR = Paths.get(System.getProperty("user.dir"));
    private final Map<String,T> map = new LinkedHashMap<>();
    private final Path filePath;
    boolean testMode = true;

    private final Lock readLock;
    private final Lock writeLock;

    public GenericFileManager(String fileName,ReentrantReadWriteLock lock) {
        writeLock = lock.writeLock();
        readLock = lock.readLock();
        Path path = Paths.get(CURRENT_DIR.toString(), "files");

        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }


        this.filePath = Paths.get(CURRENT_DIR.toString(),"files",fileName);
        File file = filePath.toFile();
        if(!file.exists() || file.length() == 0){
            try {
                Path parentDir = filePath.getParent();

                if (parentDir != null) {
                    // This method requires a Path, which parentDir is.
                    Files.createDirectories(parentDir);
                }
                if (!file.exists()) {
                    Files.createFile(file.toPath());
                }
                save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        if(!testMode) {
            try (ObjectInputStream in = new ObjectInputStream(
                    new BufferedInputStream(
                            new FileInputStream(file)))) {
                int itemsCount = in.readInt();
                for (int i = 0; i < itemsCount; i++) {
                    T item = (T) in.readObject();
                    map.put(item.getId(), item);
                }

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("An error was caught trying to read the file.");
            }
        }
        else {
            load();
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
    //or between each Predicate
    @SafeVarargs
    public final List<T> filterItems(Predicate<T>... conditions) {
        readLock.lock();
        try {
            List<T> result = new ArrayList<>();
            for (T item : map.values()) {
                for (Predicate<T> condition : conditions) {
                    if (condition.test(item)) {
                        result.add(item);
                        break;
                    }
                }
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
    }private void load() {
        writeLock.lock();
        try {
            File file = filePath.toFile();
            if (!file.exists() || file.length() == 0) {
                file.getParentFile().mkdirs();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException("Could not create file: " + file.getName(), e);
                }
                return;
            }

            try (Reader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                JsonElement classNameElement = root.get("className");
                JsonElement itemsElement = root.get("items");

                if (classNameElement != null && !classNameElement.isJsonNull()
                        && itemsElement != null && !itemsElement.isJsonNull()) {

                    String className = classNameElement.getAsString();
                    Class<?> clazz = Class.forName(className);

                    Type mapType = TypeToken.getParameterized(LinkedHashMap.class, String.class, clazz).getType();
                    LinkedHashMap<String, T> loadedMap = gson.fromJson(itemsElement, mapType);

                    map.clear();
                    if (loadedMap != null) {
                        map.putAll(loadedMap);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to load data from JSON: " + file.getName(), e);
            }
        } finally {
            writeLock.unlock();
        }
    }


    private void saveTest() {
        Path tempFilePath = filePath.getParent().resolve(filePath.getFileName().toString() + ".tmp");

        try {
            // Write to a temporary file using UTF-8
            try (BufferedWriter writer = Files.newBufferedWriter(tempFilePath, StandardCharsets.UTF_8)) {
                JsonObject root = new JsonObject();

                // Save class metadata
                String className = map.isEmpty() ? null : map.values().iterator().next().getClass().getName();
                root.addProperty("className", className);

                // gson.toJsonTree preserves order since it maps to LinkedTreeMap internally
                root.add("items", gson.toJsonTree(map));

                gson.toJson(root, writer);
            }

            // Atomic move with fallback
            try {
                Files.move(tempFilePath, filePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException e) {
                Files.move(tempFilePath, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (Exception e) {
            try {
                Files.deleteIfExists(tempFilePath);
            } catch (IOException ignored) {}
            throw new RuntimeException("Failed to save data to disk: " + filePath.getFileName(), e);
        }
    }
    private void saveInternal() {
        if(testMode){
            saveTest();
            return;
        }
        try(ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(filePath.toFile())))) {
            out.writeInt(map.size());
            for (T item : map.values()) {
                out.writeObject(item);
            }

            out.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save data to disk: " + filePath.toFile().getName(), e);
        }
    }


}

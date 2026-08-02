package FileManager;

import MainClasses.BaseClass;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
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

    private void load() {
        File file = filePath.toFile();
        // If file doesn't exist or is empty ($0$ bytes)
        if (!file.exists() || file.length() == 0) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Could not create file: " + file.getName(), e);
            }
            return;
        }

        try (Reader reader = new BufferedReader(new FileReader(file))) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonElement classNameElement = root.get("className");
            JsonElement itemsElement = root.get("items");

            if (classNameElement != null && !classNameElement.isJsonNull() && itemsElement != null) {
                String className = classNameElement.getAsString();
                Class<?> clazz = Class.forName(className);

                // Reconstruct the exact type for GSON: HashMap<String, T>
                Type mapType = TypeToken.getParameterized(HashMap.class, String.class, clazz).getType();
                HashMap<String, T> loadedMap = gson.fromJson(itemsElement, mapType);

                if (loadedMap != null) {
                    map.putAll(loadedMap);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load data from JSON: " + file.getName(), e);
        }
    }

    private void saveTest() {
        Path tempFilePath = Paths.get(filePath.toString() + ".tmp");

        try (Writer writer = new BufferedWriter(new FileWriter(tempFilePath.toFile()))) {
            JsonObject root = new JsonObject();

            // Save the class name so we know how to deserialize it later.
            // If the map is empty, we save null and handle it safely on load.
            String className = map.isEmpty() ? null : map.values().iterator().next().getClass().getName();
            root.addProperty("className", className);
            root.add("items", gson.toJsonTree(map));

            gson.toJson(root, writer);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save data to disk: " + tempFilePath.toFile().getName(), e);
        }

        // Safely swap the temp file with the actual file
        try {
            Files.move(tempFilePath, filePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new RuntimeException("Failed to atomically replace the data file.", e);
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

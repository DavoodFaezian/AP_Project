package FileManager;

import MainClasses.BaseClass;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class GenericFileManager<T extends BaseClass> {
    //get the path that the server is going to be running in.
    private static final Path CURRENT_DIR = Paths.get(System.getProperty("user.dir"));
    private final ArrayList<T> list = new ArrayList<>();
    private final File filePath;

    public GenericFileManager(String fileName) {

        this.filePath = Paths.get(CURRENT_DIR+"\\"+fileName).toFile();
        if(!filePath.exists()){
            try {
                Files.createFile(filePath.toPath());
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
                list.add(item);
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

        try(ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(filePath)))) {
            out.writeInt(list.size());
            for (T item : list) {
                out.writeObject(item);
            }

            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<T> filterItems(Predicate<T> ...conditions){
        Predicate<T> allConditions = Arrays.stream(conditions).reduce(Predicate::and).orElse(t -> false);


        return list.stream()
                .filter(allConditions)
                .toList();
    }
    public void addToList(T item){
        list.add(item);
    }
    public void removeFromList(T item){
        list.remove(item);
    }
    public void removeFromListById(String id){
        Optional<T> remove = findItemById(id);
        remove.ifPresent(this::removeFromList);


    }
    public Optional<T> findItemById(String id){
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(id)) {
                list.remove(i);
                return Optional.of(list.get(i));
            }
        }
        return Optional.empty();
    }
    public void editItem(T item){
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(item.getId())) {
                list.set(i, item);
                break;
            }
        }

    }
    public ArrayList<T> getAll() {
        return list;
    }
    public boolean exists(Predicate<T> ...conditions){
        Predicate<T> allConditions = Arrays.stream(conditions)
                .reduce(Predicate::and)
                .orElse(c->false);
        return list.stream()
                .anyMatch(allConditions);
    }

    public void removeFromListIf(Predicate<T> ...conditions) {
        var allConditions = Arrays.stream(conditions)
                                .reduce(Predicate::and)
                                .orElse(c->false);
        list.removeIf(allConditions);
    }
}

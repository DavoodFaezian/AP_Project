package FileManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileServer {

    private static final Path CURRENT_DIR = Paths.get(System.getProperty("user.dir"));

    private static final String BASE_IMAGES_FOLDER = "images";
    private static final String BASE_Format = ".bin";

    public static String savePhoto(String directory, byte[] photoData) throws IOException {
        String photoName = UUID.randomUUID().toString();

        Path dirPath = CURRENT_DIR.resolve(BASE_IMAGES_FOLDER).resolve(directory).resolve(BASE_Format);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        Path filePath = dirPath.resolve(photoName);
        Files.write(
                filePath,
                photoData,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );

        return photoName;
    }

    public static byte[] getPhoto(String directory, String photoName) throws IOException {
        Path filePath = CURRENT_DIR.resolve(BASE_IMAGES_FOLDER).resolve(directory).resolve(photoName)
                .resolve(BASE_Format);
        if (!Files.exists(filePath)) {
            return null;
        }
        return Files.readAllBytes(filePath);
    }

    public static boolean deletePhoto(String directory, String photoName) throws IOException {
        Path filePath = CURRENT_DIR.resolve(BASE_IMAGES_FOLDER).resolve(directory).resolve(photoName).resolve(BASE_Format);
        return Files.deleteIfExists(filePath);
    }



}
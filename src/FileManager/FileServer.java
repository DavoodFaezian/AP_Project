package FileManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileServer {

    private static final Path CURRENT_DIR = Paths.get(System.getProperty("user.dir"));
    private final Path filePath;
    private final Map<String , PhotoIndex> map = new HashMap<>();
    private boolean testMode = true;

    private final Lock readLock;
    private final Lock writeLock;

    public FileServer(String fileName , ReentrantReadWriteLock lock) {
        this.writeLock = lock.writeLock();
        this.readLock = lock.readLock();

        Path path = Paths.get(CURRENT_DIR.toString(), "files");

        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }


        this.filePath = Paths.get(CURRENT_DIR.toString(),"files",fileName);
        File file = filePath.toFile();
        if(!file.exists() || file.length() == 0) {
            try {
                Path parentDir = filePath.getParent();

                if (parentDir != null) {
                    // This method requires a Path, which parentDir is.
                    Files.createDirectories(parentDir);
                }
                if (!file.exists()) {
                    Files.createFile(file.toPath());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (file.length() > 0) {
            readLock.lock();
            try(DataInputStream in = new DataInputStream(
                    new BufferedInputStream(
                            new FileInputStream(file)
                    )
            )) {
                int photoCounts = in.readInt();
                for(int i = 0 ; i < photoCounts ; i++) {
                    String photoId = in.readUTF();
                    long begin = in.readLong();
                    int length = in.readInt();

                    PhotoIndex index = new PhotoIndex(photoId , begin , length);
                    map.put(photoId , index);

                    long skipped = in.skipBytes(length);
                }
            } catch(IOException e) {
                e.printStackTrace();
            } finally {
                readLock.unlock();
            }
        }
    }

    public void saveData(String photoId , byte[] photoBytes) {
        writeLock.lock();
        File file = filePath.toFile();
        long begin = file.length();
        try(DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(file , true)
                )
        )
                ) {
            out.writeUTF(photoId);
            out.writeInt(photoBytes.length);

            out.write(photoBytes);
            out.flush();

            PhotoIndex index = new PhotoIndex(photoId , begin , photoBytes.length);
            map.put(photoId , index);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            writeLock.unlock();
        }

    }

    public boolean deleteData(String photoId) {
        writeLock.lock();
        try {
            PhotoIndex removed = map.remove(photoId);
            return removed != null;
        } finally {
            writeLock.unlock();
        }
    }

    public byte[] readData(String photoData) {
        readLock.lock();
        try {
            PhotoIndex index = map.get(photoData);
            if (index == null) {
                return null;
            }
            File file = filePath.toFile();
            try(RandomAccessFile raf = new RandomAccessFile(file , "r")) {
                raf.seek(index.getBegin());

                byte[] photoBytes = new byte[index.getLength()];
                raf.read(photoBytes);
                return photoBytes;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } finally {
            readLock.unlock();
        }
    }
}
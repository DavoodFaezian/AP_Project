package MainClasses;


import java.time.LocalDateTime;
import java.util.*;

public class Album extends BaseClass<Album> {

    private String ownerId;

    private String albumName;

    private Set<String> photoIds = new HashSet<>();

    private LocalDateTime lastModified;

    private final LocalDateTime createdAt;

    public Album(String ownerId, String albumName, Set<String> photoIds) {
        this.ownerId = ownerId;
        this.albumName = albumName;
        this.photoIds = photoIds;
        createdAt = LocalDateTime.now();
    }


    public void updateTime(){
        lastModified = LocalDateTime.now();
    }

    public Set<String> getPhotoIds() {
        return photoIds;
    }

    public void setPhotoIds(Set<String> photoAlbumIds) {
        this.photoIds = photoAlbumIds;
        updateTime();
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
        updateTime();
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
        updateTime();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Album album = (Album) o;
        return Objects.equals(getId(), album.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }


    public void validateRemoveAlbum() {

    }
}

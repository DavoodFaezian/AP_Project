package MainClasses;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Album {

    private User owner;

    private String albumName;

    private List<PhotoAlbum> photos = new ArrayList<>();

    private List<AlbumShare> sharedWithUsers = new ArrayList<>();

    private final LocalDateTime createdAt;

    private LocalDateTime lastModified;

    private String id;

    public Album(User owner, String albumName, String id){
        this.owner = owner;
        this.albumName = albumName;
        this.id = id;
        this.owner.getAlbums().add(this);
        createdAt = LocalDateTime.now();
    }

    public User getOwner(){
        return owner;
    }

    public void setOwner(User owner){
        this.owner =owner;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public List<PhotoAlbum> getPhotos() {
        return photos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
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
}

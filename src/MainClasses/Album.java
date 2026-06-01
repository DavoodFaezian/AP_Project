package MainClasses;

import java.time.LocalDateTime;
import java.util.*;

public class Album extends BaseClass<Album> {

    private User owner;

    private String albumName;

    private Set<Photo> photos = new HashSet<>();

    private Set<User> sharedWithUsers = new HashSet<>();

    private final LocalDateTime createdAt;

    private LocalDateTime lastModified;

    void updateTime(){
        lastModified = LocalDateTime.now();
    }

    public Album(User owner, String albumName){
        this.owner = owner;
        this.albumName = albumName;
        this.owner.getAlbums().add(this);
        createdAt = LocalDateTime.now();
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public Set<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(Set<Photo> photos) {
        this.photos = photos;
    }

    public Set<User> getSharedWithUsers() {
        return sharedWithUsers;
    }

    public void setSharedWithUsers(Set<User> sharedWithUsers) {
        this.sharedWithUsers = sharedWithUsers;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
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

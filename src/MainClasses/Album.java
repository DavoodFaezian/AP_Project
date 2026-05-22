package MainClasses;

import java.util.ArrayList;
import java.util.List;

public class Album {

    private User owner;

    private String albumName;

    private List<Photo> photos = new ArrayList<>();

    private String id;

    public Album(User owner, String albumName , List<Photo> photos){
        this.owner = owner;
        this.albumName = albumName;
        this.photos = photos;
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

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

package MainClasses;

import java.util.ArrayList;
import java.util.List;

public class Album {

    private User owner;

    private String albumName;

    private List<PhotoAlbum> photos = new ArrayList<>();

    private String id;

    public Album(User owner, String albumName ,Photo photo){
        this.owner = owner;
        this.albumName = albumName;
        photos.add(new PhotoAlbum(photo , this));
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

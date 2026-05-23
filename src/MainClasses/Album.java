package MainClasses;

import java.util.ArrayList;
import java.util.List;

public class Album {

    private User owner;

    private String albumName;

    private List<PhotoAlbum> photos = new ArrayList<>();

    private List<AlbumShare> sharedWithUsers = new ArrayList<>();

    private String id;

    public Album(User owner, String albumName, String id){
        this.owner = owner;
        this.albumName = albumName;
        this.id = id;
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

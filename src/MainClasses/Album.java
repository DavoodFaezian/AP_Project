package MainClasses;

import java.util.ArrayList;
import java.util.List;

public class Album {

    private String albumName;

    private List<Photo> photos = new ArrayList<>();

    private String id;

    public Album(String albumName , List<Photo> photos){
        this.albumName = albumName;
        this.photos = photos;
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

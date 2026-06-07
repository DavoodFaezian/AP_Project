package MainClasses;

import Repositories.AlbumRepository;
import Repositories.PhotoRepository;

public class PhotoAlbum extends BaseClass<PhotoAlbum>{
    private String photoId;
    private String albumId;
    public PhotoAlbum(String photoId, String albumId) {
        this.photoId = photoId;
        this.albumId = albumId;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

}

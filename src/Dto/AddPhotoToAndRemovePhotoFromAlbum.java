package Dto;

public class AddPhotoToAndRemovePhotoFromAlbum {

    private String sessionId;

    private String photoId;

    private String albumId;

    public AddPhotoToAndRemovePhotoFromAlbum(String sessionId, String photoId, String albumId) {
        this.sessionId = sessionId;
        this.photoId = photoId;
        this.albumId = albumId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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

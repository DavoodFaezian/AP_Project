package Dto;

public class MovePhotoDto {

    private String sessionId;

    private String photoId;

    private String fromAlbumId;

    private String toAlbumId;

    public MovePhotoDto(String sessionId, String photoId, String fromAlbumId, String toAlbumId) {
        this.sessionId = sessionId;
        this.photoId = photoId;
        this.fromAlbumId = fromAlbumId;
        this.toAlbumId = toAlbumId;
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

    public String getFromAlbumId() {
        return fromAlbumId;
    }

    public void setFromAlbumId(String fromAlbumId) {
        this.fromAlbumId = fromAlbumId;
    }

    public String getToAlbumId() {
        return toAlbumId;
    }

    public void setToAlbumId(String toAlbumId) {
        this.toAlbumId = toAlbumId;
    }
}

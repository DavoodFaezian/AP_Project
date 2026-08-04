package DTO.Photo;

import java.util.Set;

public class EditPhotoByAlbumDto {

    private String sessionId;

    private String photoId;

    private Set<String> albumIds;

    public EditPhotoByAlbumDto(String sessionId, String photoId, Set<String> albumIds) {
        this.sessionId = sessionId;
        this.photoId = photoId;
        this.albumIds = albumIds;
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

    public Set<String> getAlbumIds() {
        return albumIds;
    }

    public void setAlbumIds(Set<String> albumIds) {
        this.albumIds = albumIds;
    }
}

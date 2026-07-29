package DTO.Photo;

public class DeletePhotoDto {

    private String sessionId;

    private String photoId;

    public DeletePhotoDto(String sessionId, String photoId) {
        this.sessionId = sessionId;
        this.photoId = photoId;
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
}

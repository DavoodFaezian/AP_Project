package DTO.Photo;

public class GetPhotoDto {
    private String sessionId;
    private String photoId;
    private String ownerId;

    public GetPhotoDto(String sessionId, String photoId, String ownerId) {
        this.sessionId = sessionId;
        this.photoId = photoId;
        this.ownerId = ownerId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getPhotoId() {
        return photoId;
    }

    public String getOwnerId() {
        return ownerId;
    }
}

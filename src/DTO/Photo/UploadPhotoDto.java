package DTO.Photo;

public class UploadPhotoDto {
    private String photoData;
    private String sessionId;
    private boolean isProfilePicture;

    public UploadPhotoDto(String photoData, String sessionId, boolean isProfilePicture) {
        this.photoData = photoData;
        this.sessionId = sessionId;
        this.isProfilePicture = isProfilePicture;
    }

    public String getPhotoData() {
        return photoData;
    }

    public String getSessionId() {
        return sessionId;
    }

    public boolean isProfilePicture() {
        return isProfilePicture;
    }
}

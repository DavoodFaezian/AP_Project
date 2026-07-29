package DTO;

public class AddProfilePhotoDto {

    private String sessionId;

    private String profilePhotoId;

    public AddProfilePhotoDto(String sessionId, String profilePhotoId) {
        this.sessionId = sessionId;
        this.profilePhotoId = profilePhotoId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getProfilePhotoId() {
        return profilePhotoId;
    }

    public void setProfilePhotoId(String profilePhotoId) {
        this.profilePhotoId = profilePhotoId;
    }
}

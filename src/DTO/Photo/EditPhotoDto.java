package DTO.Photo;

import MainClasses.Photo;

public class EditPhotoDto {

    private String sessionId;

    private PhotoDto photo;

    public EditPhotoDto(String sessionId, PhotoDto photo) {
        this.sessionId = sessionId;
        this.photo = photo;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public PhotoDto getPhoto() {
        return photo;
    }

    public void setPhotoId(PhotoDto photo) {
        this.photo = photo;
    }
}

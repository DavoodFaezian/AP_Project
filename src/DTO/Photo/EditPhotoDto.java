package DTO.Photo;

import MainClasses.Photo;

public class EditPhotoDto {

    private String sessionId;

    private Photo photo;

    public EditPhotoDto(String sessionId, Photo photo) {
        this.sessionId = sessionId;
        this.photo = photo;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhotoId(Photo photo) {
        this.photo = photo;
    }
}

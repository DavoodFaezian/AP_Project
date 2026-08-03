package DTO.Photo;

import java.util.Set;

public class AddPhotoDto {


    private String sessionId;

    private String title;
    private String photoName;

    private String albumId;

    private Set<String> tags;

    private String caption;

    private Boolean isFavorable;


    public AddPhotoDto(String sessionId,String title, String photoName, String albumId , Set<String> tags, String caption, Boolean isFavorable) {
        this.sessionId = sessionId;
        this.photoName = photoName;
        this.title = title;
        this.albumId = albumId;
        this.tags = tags;
        this.caption = caption;
        this.isFavorable = isFavorable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Boolean getFavorable() {
        return isFavorable;
    }

    public void setFavorable(Boolean favorable) {
        isFavorable = favorable;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}

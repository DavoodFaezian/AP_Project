package DTO.Photo;

import java.util.Set;

public class AddPhotoDto {


    private String sessionId;

    private String name;

    private String albumId;

    private Set<String> tags;

    private String caption;

    private Boolean isFavorable;

    private String photoData;

    public AddPhotoDto(String sessionId, String name, String albumId , Set<String> tags, String caption, Boolean isFavorable, String photoData) {
        this.sessionId = sessionId;
        this.name = name;
        this.albumId = albumId;
        this.tags = tags;
        this.caption = caption;
        this.isFavorable = isFavorable;
        this.photoData = photoData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPhotoData() {
        return photoData;
    }

    public void setPhotoData(String photoData) {
        this.photoData = photoData;
    }
}

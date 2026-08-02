package DTO.Photo;

import MainClasses.Photo;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class PhotoDto {
    private String id;
    private String ownerId;

    private String photoName;

    private Set<String> tags;

    private String caption;

    private Boolean isFavorable;

    private LocalDateTime lastModified;

    private Set<String> commentIds = new HashSet<>();

    private Set<String> albumIds = new HashSet<>();
    private Set<String> postIds = new HashSet<>();

    private LocalDateTime createdAt;

    public PhotoDto(Photo photo) {
        this.id = photo.getId();
        //complete the rest
        this.id = photo.getId();
        this.ownerId = photo.getOwnerId();
        this.photoName = photo.getPhotoName();
        this.tags = photo.getTags() != null ? new HashSet<>(photo.getTags()) : new HashSet<>();
        this.caption = photo.getCaption();
        this.isFavorable = photo.getFavorable();
        this.lastModified = photo.getLastModified();
        this.commentIds = photo.getCommentIds() != null ? new HashSet<>(photo.getCommentIds()) : new HashSet<>();
        this.albumIds = photo.getAlbumIds() != null ? new HashSet<>(photo.getAlbumIds()) : new HashSet<>();
        this.postIds = photo.getPostIds() != null ? new HashSet<>(photo.getPostIds()) : new HashSet<>();
        this.createdAt = photo.getCreatedAt();
    }

    public String getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getPhotoName() {
        return photoName;
    }

    public Set<String> getTags() {
        return tags;
    }

    public String getCaption() {
        return caption;
    }

    public Boolean getFavorable() {
        return isFavorable;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public Set<String> getCommentIds() {
        return commentIds;
    }

    public Set<String> getAlbumIds() {
        return albumIds;
    }

    public Set<String> getPostIds() {
        return postIds;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

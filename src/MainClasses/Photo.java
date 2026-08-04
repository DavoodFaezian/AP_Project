package MainClasses;

import java.time.LocalDateTime;
import java.util.*;

public class Photo extends BaseClass<Photo>{

    private String ownerId;

    private String photoName;
    private String title;

    private Set<String> tags;

    private String caption;

    private Boolean isFavorable;

    private LocalDateTime lastModified;

    private Set<String> commentIds = new HashSet<>();

    private Set<String> albumIds = new HashSet<>();
    private Set<String> postIds = new HashSet<>();

    private final LocalDateTime createdAt;

    void updateTime(){
        lastModified = LocalDateTime.now();
    }

    public Photo(String ownerId, String photoName, Set<String> albumIds , Set<String> tags, String caption, Boolean isFavorable, String title) {
        this.ownerId = ownerId;
        this.photoName = photoName;
        this.tags = tags;
        this.caption = caption;
        this.isFavorable = isFavorable;
        this.albumIds = albumIds;
        createdAt = LocalDateTime.now();
        this.title = title;
    }


    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
        updateTime();
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
        updateTime();
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
        updateTime();
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
        updateTime();
    }

    public Boolean getFavorable() {
        return isFavorable;
    }

    public void setFavorable(Boolean favorable) {
        isFavorable = favorable;
    }

    public Set<String> getCommentIds() {
        return commentIds;
    }

    public void setCommentIds(Set<String> commentIds) {
        this.commentIds = commentIds;
        updateTime();
    }

    public Set<String> getAlbumIds() {
        return albumIds;
    }

    public void setAlbumIds(Set<String> photoAlbumIds) {
        this.albumIds = photoAlbumIds;
        updateTime();
    }

    public Set<String> getPostIds() {
        return postIds;
    }
    public void setPostIds(Set<String> postIds) {
        this.postIds = postIds;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return Objects.equals(getId(), photo.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }


}

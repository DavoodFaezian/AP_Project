package MainClasses;

import java.time.LocalDateTime;
import java.util.*;

public class Post extends BaseClass<Post>{
    private String ownerId;
    private Set<String> photoIds;
    private Set<String> albumIds;
    private Set<String> commentIds = new LinkedHashSet<>();
    private Boolean commentsAllowed;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;

    public Post(String ownerId, Set<String> photoIds, Set<String> albumIds, Boolean commentsAllowed) {
        this.ownerId = ownerId;
        this.photoIds = photoIds;
        this.albumIds = albumIds;
        this.commentsAllowed = commentsAllowed;
        this.createdAt = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
    }
    public void updateTime(){
        this.lastModified = LocalDateTime.now();
    }
    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Set<String> getPhotoIds() {
        return photoIds;
    }

    public void setPhotoIds(Set<String> photoIds) {
        this.photoIds = photoIds;
    }

    public Set<String> getAlbumIds() {
        return albumIds;
    }

    public void setAlbumIds(Set<String> albumIds) {
        this.albumIds = albumIds;
    }

    public Set<String> getCommentIds() {
        return commentIds;
    }

    public void setCommentIds(Set<String> commentIds) {
        this.commentIds = commentIds;
    }

    public Boolean getCommentsAllowed() {
        return commentsAllowed;
    }

    public void setAreCommentsAllowed(Boolean commentsAllowed) {
        this.commentsAllowed = commentsAllowed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

}

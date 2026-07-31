package MainClasses;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Post extends BaseClass<Post>{
    private String ownerId;
    private Set<String> photoIds;
    private Set<String> albumIds;
    private Set<String> sharedUserIds;
    private Set<String> commentIds;
    private Boolean commentsAllowed;

    public Post(String ownerId, Set<String> photoIds, Set<String> albumIds, Boolean commentsAllowed) {
        this.ownerId = ownerId;
        this.photoIds = photoIds;
        this.albumIds = albumIds;
        this.commentsAllowed = commentsAllowed;
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

    public Set<String> getSharedUserIds() {
        return sharedUserIds;
    }

    public void setSharedUserIds(Set<String> sharedUserIds) {
        this.sharedUserIds = sharedUserIds;
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
}

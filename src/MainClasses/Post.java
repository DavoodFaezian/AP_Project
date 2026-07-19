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

    public Post(String ownerId, Set<String> photoIds, Set<String> albumIds, Set<String> sharedUserIds) {
        this.ownerId = ownerId;
        this.photoIds = photoIds;
        this.albumIds = albumIds;
        this.sharedUserIds = sharedUserIds;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(ownerId, post.ownerId) && Objects.equals(photoIds, post.photoIds) && Objects.equals(albumIds, post.albumIds) && Objects.equals(sharedUserIds, post.sharedUserIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerId, photoIds, albumIds, sharedUserIds);
    }
}

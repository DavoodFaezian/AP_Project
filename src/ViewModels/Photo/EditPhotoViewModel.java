package ViewModels.Photo;

import MainClasses.Comment;

import java.util.HashSet;
import java.util.Set;

public class EditPhotoViewModel {
    private String id;
    private String photoName;
    private Set<String> tags;
    private String caption;
    private Boolean isFavorable;
    private Set<String> albumIds = new HashSet<>();
    private Boolean permissionForLeavingComment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
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

    public Set<String> getAlbumIds() {
        return albumIds;
    }

    public void setAlbumIds(Set<String> albumIds) {
        this.albumIds = albumIds;
    }

    public Boolean getPermissionForLeavingComment() {
        return permissionForLeavingComment;
    }

    public void setPermissionForLeavingComment(Boolean permissionForLeavingComment) {
        this.permissionForLeavingComment = permissionForLeavingComment;
    }
}

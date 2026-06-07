package ViewModels.Photo;

import MainClasses.Photo;

import java.util.HashSet;
import java.util.Set;

public class PhotoViewModel {
    private String photoName;
    private Set<String> tags;
    private String caption;
    private Boolean isFavorable;
    private Boolean permissionForLeavingComment;

    public PhotoViewModel(String photoName, Set<String> tags, String caption, Boolean isFavorable, Boolean permissionForLeavingComment) {
        this.photoName = photoName;
        this.tags = tags;
        this.caption = caption;
        this.isFavorable = isFavorable;
        this.permissionForLeavingComment = permissionForLeavingComment;
    }
    public PhotoViewModel(Photo photo){
        this.photoName = photo.getPhotoName();
        this.tags = photo.getTags();
        this.caption = photo.getCaption();
        this.isFavorable = photo.isFavorable();
        this.permissionForLeavingComment = photo.isPermissionForLeavingComment();
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

    public Boolean getPermissionForLeavingComment() {
        return permissionForLeavingComment;
    }

    public void setPermissionForLeavingComment(Boolean permissionForLeavingComment) {
        this.permissionForLeavingComment = permissionForLeavingComment;
    }
}

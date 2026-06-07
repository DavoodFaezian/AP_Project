package MainClasses;

import Exceptions.*;
import Repositories.AlbumRepository;
import Repositories.CommentRepository;
import Services.PhotoAlbumService;
import ViewModels.Photo.CreatePhotoViewModel;
import ViewModels.Photo.EditPhotoViewModel;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Photo extends BaseClass<Photo>{

    private String ownerId;

    private String photoName;

    private Set<String> tags;

    private String caption;

    private Boolean isFavorable;


    private Boolean permissionForLeavingComment;

    private LocalDateTime dateOfShare;

    private LocalDateTime lastModified;
    private Set<String > commentIds = new HashSet<>();
    private Set<String> albumIds = new HashSet<>();

    private Set<String> sharedUserIds = new HashSet<>();

    private final LocalDateTime createdAt;

    void updateTime(){
        lastModified = LocalDateTime.now();
    }

    public void updateDateOfShare(){
        dateOfShare = LocalDateTime.now();
    }

    public Photo(String ownerId, String photoName, Set<String> tags, String caption, Boolean isFavorable, Boolean permissionForLeavingComment, Set<String> albumIds) {
        this.ownerId = ownerId;
        this.photoName = photoName;
        this.tags = tags;
        this.caption = caption;
        this.isFavorable = isFavorable;
        this.permissionForLeavingComment = permissionForLeavingComment;
        this.albumIds = albumIds;
        createdAt = LocalDateTime.now();

    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
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

    public Boolean isPermissionForLeavingComment() {
        return permissionForLeavingComment;
    }

    public void setPermissionForLeavingComment(Boolean permissionForLeavingComment) {
        this.permissionForLeavingComment = permissionForLeavingComment;
    }

    public Set<String> getCommentIds() {
        return commentIds;
    }

    public void setCommentIds(Set<String> commentIds) {
        this.commentIds = commentIds;
    }

    public Set<String> getPhotoAlbumIds() {
        return albumIds;
    }

    public void setPhotoAlbumIds(Set<String> photoAlbumIds) {
        this.albumIds = photoAlbumIds;
    }

    public Set<String> getSharedUserIds() {
        return sharedUserIds;
    }

    public void setSharedUserIds(Set<String> sharedUserIds) {
        this.sharedUserIds = sharedUserIds;
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

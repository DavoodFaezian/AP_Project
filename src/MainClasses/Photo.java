package MainClasses;

import Exceptions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Photo extends BaseClass<Photo>{

    private User owner;

    private String photoName;

    private List<Comment> comments;

    private List<String> tags;

    private List<String> captions;

    private Boolean isFavorable;

    private List<Album> albums = new ArrayList<>();

    private final PhotoAlbum service = new PhotoAlbum();

    private Boolean permissionForLeavingComment;

    private LocalDateTime dateOfShare;

    private List<PhotoShare> sharedWithUsers = new ArrayList<>();

    private final LocalDateTime createdAt;

    void updateTime(){
        LocalDateTime lastModified = LocalDateTime.now();
    }

    public Photo(User owner , List<String> captions, String photoName, List<Comment> comments, List<String> tags, Boolean isFavorable, Album album, Boolean permissionForLeavingComment) {
        this.owner = owner;
        this.captions = captions;
        this.photoName = photoName;
        this.comments = comments;
        this.tags = tags;
        this.isFavorable = isFavorable;
        this.permissionForLeavingComment = permissionForLeavingComment;
        service.addPhotoToAlbum(this , album);
        this.owner.getPhotos().add(this);
        createdAt = LocalDateTime.now();
    }

    public User getOwner(){
        return owner;
    }

    public String getPhotoName() {
        return photoName;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<String> getCaptions() {
        return captions;
    }

    public Boolean isFavorable() {
        return isFavorable;
    }

    public Boolean isPermissionForLeavingComment() {
        return permissionForLeavingComment;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    private <T> void validateParameter(T parameter){
        if(parameter == null){
            throw new NullPointerException("Parameter is null!!!");
        }
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setOwner(User owner){
        this.owner = owner;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
        updateTime();
    }

    private void validatePermission(boolean permissionForLeavingComment){
         if(!permissionForLeavingComment){
             throw new AccessDeniedException("You can't leave comment for this photo!!!");
         }
    }

    public void setComments(List<Comment> comments) {
        validatePermission(this.permissionForLeavingComment);
        this.comments = comments;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
        updateTime();
    }

    public void setCaptions(List<String> captions) {
        this.captions = captions;
        updateTime();
    }

    public void setFavorable(Boolean favorable) {
        isFavorable = favorable;
        updateTime();
    }

    public void setPermissionForLeavingComment(Boolean permissionForLeavingComment) {
        this.permissionForLeavingComment = permissionForLeavingComment;
        updateTime();
    }

    public void addComment(Comment comment){
        validatePermission(this.permissionForLeavingComment);
        validateParameter(comment);
        this.comments.add(comment);
    }

    public void removeComment(Comment comment){
        validatePermission(this.permissionForLeavingComment);
        this.comments.remove(comment);
    }

    private void updateDateOfShare(){
        dateOfShare = LocalDateTime.now();
    }

    public void sharePhoto(User user){
        validateParameter(user);
        this.sharedWithUsers.add(new PhotoShare(this.owner , user , this));
        updateDateOfShare();
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

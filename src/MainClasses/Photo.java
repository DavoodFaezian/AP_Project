package MainClasses;

import Exceptions.*;

import java.time.LocalDateTime;
import java.util.*;

public class Photo extends BaseClass<Photo>{

    private User owner;

    private String photoName;

    private Set<Comment> comments = new HashSet<>();

    private Set<String> tags;

    private String caption;

    private Boolean isFavorable;

    private Set<Album> albums = new HashSet<>();

    private Boolean permissionForLeavingComment;

    private LocalDateTime dateOfShare;

    private LocalDateTime lastModified;

    private Set<User> sharedWithUsers = new HashSet<>();

    private final LocalDateTime createdAt;

    void updateTime(){
        lastModified = LocalDateTime.now();
    }

    void updateDateOfShare(){
        dateOfShare = LocalDateTime.now();
    }

    public Photo(User owner , String caption, String photoName,Set<String> tags, Boolean isFavorable, Album album, Boolean permissionForLeavingComment) {
        this.owner = owner;
        this.caption = caption;
        this.photoName = photoName;
        this.tags = tags;
        this.isFavorable = isFavorable;
        this.permissionForLeavingComment = permissionForLeavingComment;
        PhotoAlbum service = new PhotoAlbum();
        service.addPhotoToAlbum(this , album);
        owner.getPhotos().add(this);
        createdAt = LocalDateTime.now();
    }


    public Set<User> getSharedWithUsers() {
        return sharedWithUsers;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getDateOfShare() {
        return dateOfShare;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public User getOwner(){
        return owner;
    }

    public String getPhotoName() {
        return photoName;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public Set<String> getTags() {
        return tags;
    }

    public String getCaption() {
        return caption;
    }

    public Boolean isFavorable() {
        return isFavorable;
    }

    public Boolean isPermissionForLeavingComment() {
        return permissionForLeavingComment;
    }

    public void setAlbums(Set<Album> albums) {
        this.albums = albums;
    }

    private <T> void validateParameter(T parameter){
        if(parameter == null){
            throw new NullPointerException("Parameter is null!!!");
        }
    }

    private <T> void validateMemberShip(Set<T> parameters , T parameter){
        if(!parameters.contains(parameter)){
            throw new ItemDoesNotExistException("Item wasn't found!!!");
        }
    }

    public Set<Album> getAlbums() {
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

    public void setComments(Set<Comment> comments) {
        validatePermission(this.permissionForLeavingComment);
        this.comments = comments;
        updateTime();
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
        updateTime();
    }

    public void setCaption(String caption) {
        this.caption = caption;
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

    public void setSharedWithUsers(Set<User> sharedWithUsers) {
        this.sharedWithUsers = sharedWithUsers;
    }

    public void addComment(Comment comment){
        validatePermission(this.permissionForLeavingComment);
        validateParameter(comment);
        this.comments.add(comment);
        updateTime();
    }

    public void removeComment(Comment comment){
        validatePermission(this.permissionForLeavingComment);
        this.comments.remove(comment);
        updateTime();
    }

    public void editComment(Comment comment , String script){
        validatePermission(this.permissionForLeavingComment);
        validateParameter(comment);
        validateMemberShip(this.comments , comment);
        comment.setScript(script);
        updateTime();
    }

    public void addTag(String tag){
        validateParameter(tag);
        validateMemberShip(tags , tag);
        tags.add(tag);
        updateTime();
    }

    public void removeTag(String tag){
        validateParameter(tag);
        tags.remove(tag);
        updateTime();
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

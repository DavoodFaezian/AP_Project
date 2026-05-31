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

    private List<PhotoAlbum> albums = new ArrayList<>();

    private Boolean permissionForLeavingComment;

    private LocalDateTime dateOfShare;

    private List<PhotoShare> sharedWithUsers = new ArrayList<>();

    private final LocalDateTime createdAt;

    private LocalDateTime lastModified;

    private void updateTime(){
        lastModified = LocalDateTime.now();
    }

    public Photo(User owner , List<String> captions, String photoName, List<Comment> comments, List<String> tags, Boolean isFavorable, Album album, Boolean permissionForLeavingComment) {
        this.owner = owner;
        this.captions = captions;
        this.photoName = photoName;
        this.comments = comments;
        this.tags = tags;
        this.isFavorable = isFavorable;
        this.permissionForLeavingComment = permissionForLeavingComment;
        if(album != null){
            albums.add(new PhotoAlbum(this , album));
            album.getPhotos().add(new PhotoAlbum(this , album));
        }
        else{
            albums.add(new PhotoAlbum(this , null));
        }
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

    public void setAlbums(List<PhotoAlbum> albums) {
        this.albums = albums;
    }

    private void validateUser(User user){
        if(user == null){
            throw new NullPointerException("User is null!!!");
        }
    }

    public List<PhotoAlbum> getAlbums() {
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

    private void validateForAdding(Album album){
        if(albums.contains(new PhotoAlbum(this , album))){
            throw new PhotoIsAlreadyExistsException("Photo is already exists!!!");
        }
    }

    public void addPhotoToAlbum(Album album){
        validateForAdding(album);
        albums.add(new PhotoAlbum(this , album));
        if(album != null) {
            album.getPhotos().add(new PhotoAlbum(this, album));
            album.setLastModified(LocalDateTime.now());
        }
    }

    private void validateForRemoving(Album album){
        if(!albums.contains(new PhotoAlbum(this , album))){
            throw new PhotoDoesNotExistException("Photo does not exist!!!");
        }
    }

    public void removePhotoFromAlbum(Album album){
        validateForRemoving(album);
        albums.remove(new PhotoAlbum(this , album));
        if (album != null) {
            album.getPhotos().remove(new PhotoAlbum(this , album));
            album.setLastModified(LocalDateTime.now());
        }
        if(albums.isEmpty()){
            owner.getPhotos().remove(this);
        }
    }

    public void validateForTransferring(Album fromAlbum , Album toAlbum){
        if(!albums.contains(new PhotoAlbum(this , fromAlbum))){
            throw new PhotoDoesNotExistException("Photo does not exist in original album!!!");
        }if(albums.contains(new PhotoAlbum(this , toAlbum))){
            throw new PhotoIsAlreadyExistsException("Photo is already exists in destination album!!!");
        }
    }

    public void transferPhoto(Album fromAlbum , Album toAlbum){
        validateForTransferring(fromAlbum , toAlbum);
        albums.remove(new PhotoAlbum(this , fromAlbum));
        albums.add(new PhotoAlbum(this , toAlbum));
        if (fromAlbum != null && toAlbum != null) {
            fromAlbum.getPhotos().remove(new PhotoAlbum(this , fromAlbum));
            toAlbum.getPhotos().add(new PhotoAlbum(this , toAlbum));
            fromAlbum.setLastModified(LocalDateTime.now());
            toAlbum.setLastModified(LocalDateTime.now());
        }
        else if (fromAlbum != null) {
            fromAlbum.getPhotos().remove(new PhotoAlbum(this, fromAlbum));
            fromAlbum.setLastModified(LocalDateTime.now());
        }
        else if (toAlbum != null) {
            toAlbum.getPhotos().add(new PhotoAlbum(this, toAlbum));
            toAlbum.setLastModified(LocalDateTime.now());
        }
    }

    private void updateDateOfShare(){
        dateOfShare = LocalDateTime.now();
    }

    public void sharePhoto(User user){
        validateUser(user);
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

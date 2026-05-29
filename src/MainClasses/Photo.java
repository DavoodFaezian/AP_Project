package MainClasses;

import Exceptions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Photo implements Comparable<Photo>{

    private User owner;

    private String photoName;

    private List<Comment> comments;

    private List<String> tags;

    private List<String> captions;

    private Boolean isFavorable;

    private List<PhotoAlbum> albums = new ArrayList<>();

    private Boolean permissionForLeavingComment;

    private LocalDateTime dateOfUpload;

    private List<PhotoShare> sharedWithUsers = new ArrayList<>();

    private String id;

    public Photo(User owner , List<String> captions, String photoName, List<Comment> comments, List<String> tags, Boolean isFavorable, Album album, Boolean permissionForLeavingComment, LocalDateTime dateOfUpload, String id) {
        this.owner = owner;
        this.captions = captions;
        this.photoName = photoName;
        this.comments = comments;
        this.tags = tags;
        this.isFavorable = isFavorable;
        this.permissionForLeavingComment = permissionForLeavingComment;
        this.dateOfUpload = dateOfUpload;
        this.id = id;
        if(album != null){
            albums.add(new PhotoAlbum(this , album));
            album.getPhotos().add(new PhotoAlbum(this , album));
        }
        else{
            albums.add(new PhotoAlbum(this , null));
        }
        this.owner.getPhotos().add(this);
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

    public LocalDateTime getDateOfUpload() {
        return dateOfUpload;
    }

    public List<PhotoAlbum> getAlbums() {
        return albums;
    }

    public String getId() {
        return id;
    }

    public void setOwner(User owner){
        this.owner = owner;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setCaptions(List<String> captions) {
        this.captions = captions;
    }

    public void setFavorable(Boolean favorable) {
        isFavorable = favorable;
    }

    public void setPermissionForLeavingComment(Boolean permissionForLeavingComment) {
        this.permissionForLeavingComment = permissionForLeavingComment;
    }

    public void setDateOfUpload(LocalDateTime dateOfUpload) {
        this.dateOfUpload = dateOfUpload;
    }

    public void setId(String id) {
        this.id = id;
    }

    private void validateAccess(Album album){
        if(album != null)
            if(!album.getOwner().equals(this.owner)){
                throw new NotOwnersAlbumException("Access denied.");
            }
    }

    private void validateForAdding(Album album){
        validateAccess(album);
        if(albums.contains(new PhotoAlbum(this , album))){
            throw new PhotoIsAlreadyExistsException("Photo is already exists.");
        }
    }

    public void addPhotoToAlbum(Album album){
        validateForAdding(album);
        albums.add(new PhotoAlbum(this , album));
        if(album != null)
            album.getPhotos().add(new PhotoAlbum(this , album));
    }

    private void validateForRemoving(Album album){
        validateAccess(album);
        if(!albums.contains(new PhotoAlbum(this , album))){
            throw new PhotoDoesNotExistException("Photo does not exist.");
        }
    }

    public void removePhotoFromAlbum(Album album){
        validateForRemoving(album);
        albums.remove(new PhotoAlbum(this , album));
        if (album != null) {
            album.getPhotos().remove(new PhotoAlbum(this , album));
        }
        if(albums.isEmpty()){
            owner.getPhotos().remove(this);
        }
    }

    public void validateForTransferring(Album fromAlbum , Album toAlbum){
        validateAccess(fromAlbum);
        validateAccess(toAlbum);
        if(albums.contains(new PhotoAlbum(this , toAlbum))){
            throw new PhotoIsAlreadyExistsException("Photo is already exists in destination album.");
        }
        if(!albums.contains(new PhotoAlbum(this , fromAlbum))){
            throw new PhotoDoesNotExistException("Photo does not exist in original album.");
        }
    }

    public void transferPhoto(Album fromAlbum , Album toAlbum){
        validateForTransferring(fromAlbum , toAlbum);
        albums.remove(new PhotoAlbum(this , fromAlbum));
        albums.add(new PhotoAlbum(this , toAlbum));
        if (fromAlbum != null && toAlbum != null) {
            fromAlbum.getPhotos().remove(new PhotoAlbum(this , fromAlbum));
            toAlbum.getPhotos().add(new PhotoAlbum(this , toAlbum));
        } if (fromAlbum != null){
            fromAlbum.getPhotos().remove(new PhotoAlbum(this , fromAlbum));
        } if (toAlbum != null) {
            toAlbum.getPhotos().add(new PhotoAlbum(this , toAlbum));
        }
    }

    @Override
    public int compareTo(Photo o) {
        if(this.dateOfUpload.isBefore(o.getDateOfUpload())) return -1;
        if(this.dateOfUpload.isEqual(o.getDateOfUpload())) return 0;
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return Objects.equals(getOwner(), photo.getOwner()) && Objects.equals(getPhotoName(), photo.getPhotoName()) && Objects.equals(getComments(), photo.getComments()) && Objects.equals(getTags(), photo.getTags()) && Objects.equals(getCaptions(), photo.getCaptions()) && Objects.equals(isFavorable, photo.isFavorable) && Objects.equals(getAlbums(), photo.getAlbums()) && Objects.equals(permissionForLeavingComment, photo.permissionForLeavingComment) && Objects.equals(getDateOfUpload(), photo.getDateOfUpload()) && Objects.equals(sharedWithUsers, photo.sharedWithUsers) && Objects.equals(getId(), photo.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOwner(), getPhotoName(), getComments(), getTags(), getCaptions(), isFavorable, getAlbums(), permissionForLeavingComment, getDateOfUpload(), sharedWithUsers, getId());
    }
}

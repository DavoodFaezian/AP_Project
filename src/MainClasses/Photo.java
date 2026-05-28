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

    public void addPhotoToAlbum(Album album){
        if(album == null && albums.contains(new PhotoAlbum(this , null))){
            throw new PhotoIsAlreadyExistsException("Photo is already exists.");
        }
        if (album != null) {
            if(!album.getOwner().equals(this.owner)){
                throw new NotOwnersAlbumException("You can't add your photo to someone else's album.");
            }
            if(albums.contains(new PhotoAlbum(this , album))){
                throw new PhotoIsAlreadyExistsInThisAlbumException("Photo is Already exists in this album.");
            }
            PhotoAlbum photoAlbum = new PhotoAlbum(this , album);
            albums.add(photoAlbum);
            album.getPhotos().add(photoAlbum);
        }
    }

    public void removePhotoFromAlbum(Album album){
        if(album == null && !albums.contains(new PhotoAlbum(this , null))){
            throw new PhotoDoesNotExistException("Photo does not exist.");
        }
        if(album == null && albums.contains(new PhotoAlbum(this , null))){
            albums.remove(new PhotoAlbum(this , null));
            if(albums.isEmpty()){
                owner.getPhotos().remove(this);
            }
        }
        if (album != null) {
            if(!album.getOwner().equals(this.owner)){
                throw new NotOwnersAlbumException("You can't remove your photo from someone else's album.");
            }
            if(!albums.contains(new PhotoAlbum(this , album))){
                throw new PhotoDoesNotExistInThisAlbum("Photo does not exist in this album.");
            }
            albums.remove(new PhotoAlbum(this , album));
            album.getPhotos().remove(new PhotoAlbum(this , album));
            if(albums.isEmpty()){
                owner.getPhotos().remove(this);
            }
        }
    }

    public void transferPhoto(Album fromAlbum , Album toAlbum){
        if(fromAlbum != null && toAlbum != null){
            if(!fromAlbum.getOwner().equals(this.owner) || !toAlbum.getOwner().equals(this.owner)){
                throw new NotOwnersAlbumException("Access denied.");
            }
            if(!albums.contains(new PhotoAlbum(this , fromAlbum))){
                throw new PhotoDoesNotExistInOriginalAlbumException("Photo does not exist in original album.");
            }
            if(albums.contains(new PhotoAlbum(this , toAlbum))){
                throw new PhotoIsAlreadyExistsInDestinationAlbumException("Photo is already exists in destination album.");
            }
            albums.remove(new PhotoAlbum(this , fromAlbum));
            fromAlbum.getPhotos().remove(new PhotoAlbum(this , fromAlbum));
            albums.add(new PhotoAlbum(this , toAlbum));
            toAlbum.getPhotos().add(new PhotoAlbum(this , toAlbum));
        }
        if(toAlbum == null && fromAlbum == null){
            throw new InvalidTransferException("Invalid transfer.");
        }
        if(toAlbum == null){
            transferWithOutDestinationAlbum(fromAlbum);
        }
        if(fromAlbum == null){
            transferWithoutOriginalAlbum(toAlbum);
        }
    }

    private void transferWithoutOriginalAlbum(Album toAlbum){
        if(!toAlbum.getOwner().equals(this.owner)){
            throw new NotOwnersAlbumException("Access denied.");
        }
        if(!albums.contains(new PhotoAlbum(this , null))){
            throw new PhotoDoesNotExistException("Photo does not exist here.");
        }
        if(albums.contains(new PhotoAlbum(this , toAlbum))){
            throw new PhotoIsAlreadyExistsInDestinationAlbumException("Photo is already exists in destination album.");
        }
        albums.remove(new PhotoAlbum(this , null));
        albums.add(new PhotoAlbum(this , toAlbum));
        toAlbum.getPhotos().add(new PhotoAlbum(this , toAlbum));
    }

    private void transferWithOutDestinationAlbum(Album fromAlbum){
        if(!fromAlbum.getOwner().equals(this.owner)){
            throw new NotOwnersAlbumException("Access denied.");
        }
        if(!albums.contains(new PhotoAlbum(this , fromAlbum))){
            throw new PhotoDoesNotExistInOriginalAlbumException("Photo does not exist in original album.");
        }
        if(albums.contains(new PhotoAlbum(this , null))){
            throw new PhotoIsAlreadyExistsException("Photo is already exists there.");
        }
        albums.remove(new PhotoAlbum(this , fromAlbum));
        fromAlbum.getPhotos().remove(new PhotoAlbum(this , fromAlbum));
        albums.add(new PhotoAlbum(this , null));
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

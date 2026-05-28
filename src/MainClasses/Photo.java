package MainClasses;

import Exceptions.NotOwnersAlbumException;
import Exceptions.PhotoDoesNotExistInThisAlbum;
import Exceptions.PhotoIsAlreadyExistsInThisAlbumException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        if(album == null){
            throw new NullPointerException("The parameter is null.");
        }
        if(!album.getOwner().equals(this.owner)){
            throw new NotOwnersAlbumException("You can't add your photo to someone else's album.");
        }
        for(PhotoAlbum a : albums){
            if(a.album().equals(album)){
                throw new PhotoIsAlreadyExistsInThisAlbumException("Photo is Already exists in this album.");
            }
        }
        PhotoAlbum photoAlbum = new PhotoAlbum(this , album);
        albums.add(photoAlbum);
        album.getPhotos().add(photoAlbum);
    }

    public void removePhotoFromAlbum(Album album){
        if(album == null){
            throw new NullPointerException("The parameter is null.");
        }
        if(!album.getOwner().equals(this.owner)){
            throw new NotOwnersAlbumException("You can't remove your photo from someone else's album.");
        }
        boolean isPhotoExistsInThisAlbum = false;
        for(PhotoAlbum a : albums){
            if (a.album().equals(album)) {
                isPhotoExistsInThisAlbum = true;
                break;
            }
        }
        if(!isPhotoExistsInThisAlbum){
            throw new PhotoDoesNotExistInThisAlbum("The photo does not exist in this album.");
        }
    }

    @Override
    public int compareTo(Photo o) {
        if(this.dateOfUpload.isBefore(o.getDateOfUpload())) return -1;
        if(this.dateOfUpload.isEqual(o.getDateOfUpload())) return 0;
        return 1;
    }
}

package MainClasses;

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
        }
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

    public boolean addPhotoToAlbum(Album album){
        if(album == null){
            return false;
        }
        PhotoAlbum photoAlbum = new PhotoAlbum(this , album);
        albums.add(photoAlbum);
        album.getPhotos().add(photoAlbum);
        return true;
    }

    @Override
    public int compareTo(Photo o) {
        if(this.dateOfUpload.isBefore(o.getDateOfUpload())) return -1;
        if(this.dateOfUpload.isEqual(o.getDateOfUpload())) return 0;
        return 1;
    }
}

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

    private boolean isFavorable;

    private List<PhotoAlbum> albums = new ArrayList<>();

    private boolean permissionForWritingComment = true;

    private LocalDateTime dateOfUpload;

    public Photo(User owner , List<String> captions, String photoName, List<Comment> comments, List<String> tags, boolean isFavorable, Album album, boolean permissionForWritingComment, LocalDateTime dateOfUpload) {
        this.owner = owner;
        this.captions = captions;
        this.photoName = photoName;
        this.comments = comments;
        this.tags = tags;
        this.isFavorable = isFavorable;
        this.permissionForWritingComment = permissionForWritingComment;
        this.dateOfUpload = dateOfUpload;
        albums.add(new PhotoAlbum(this , album));
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

    public boolean isFavorable() {
        return isFavorable;
    }

    public boolean isPermissionForWritingComment() {
        return permissionForWritingComment;
    }

    public LocalDateTime getDateOfUpload() {
        return dateOfUpload;
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

    public void setFavorable(boolean favorable) {
        isFavorable = favorable;
    }

    public void setPermissionForWritingComment(boolean permissionForWritingComment) {
        this.permissionForWritingComment = permissionForWritingComment;
    }

    public void setDateOfUpload(LocalDateTime dateOfUpload) {
        this.dateOfUpload = dateOfUpload;
    }

    @Override
    public int compareTo(Photo o) {
        if(this.dateOfUpload.isBefore(o.getDateOfUpload())) return -1;
        if(this.dateOfUpload.isEqual(o.getDateOfUpload())) return 0;
        return 1;
    }
}

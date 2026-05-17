package MainClasses;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Photo {

    private byte[] information;

    private String photoName;

    private List<Comment> comments = new ArrayList<>();

    private List<String> tags = new ArrayList<>();

    private List<String> captions = new ArrayList<>();

    private boolean isFavorable;

    private Album album;

    private boolean permissionForWritingComment = true;

    private LocalDateTime dateOfUpload;

    public Photo(List<String> captions, byte[] information, String photoName, List<Comment> comments, List<String> tags, boolean isFavorable, Album album, boolean permissionForWritingComment, LocalDateTime dateOfUpload) {
        this.captions = captions;
        this.information = information;
        this.photoName = photoName;
        this.comments = comments;
        this.tags = tags;
        this.isFavorable = isFavorable;
        this.album = album;
        this.permissionForWritingComment = permissionForWritingComment;
        this.dateOfUpload = dateOfUpload;
    }

    public byte[] getInformation() {
        return information;
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

    public Album getAlbum() {
        return album;
    }

    public boolean isPermissionForWritingComment() {
        return permissionForWritingComment;
    }

    public LocalDateTime getDateOfUpload() {
        return dateOfUpload;
    }

    public void setInformation(byte[] information) {
        this.information = information;
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

    public void setAlbum(Album album) {
        this.album = album;
    }

    public void setPermissionForWritingComment(boolean permissionForWritingComment) {
        this.permissionForWritingComment = permissionForWritingComment;
    }

    public void setDateOfUpload(LocalDateTime dateOfUpload) {
        this.dateOfUpload = dateOfUpload;
    }
}

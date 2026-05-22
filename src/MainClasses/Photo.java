package MainClasses;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Photo implements Comparable<Photo>{

    private String photoName;

    private List<Comment> comments = new ArrayList<>();

    private List<String> tags = new ArrayList<>();

    private List<String> captions = new ArrayList<>();

    private boolean isFavorable;

    private List<Album> albums;

    private boolean permissionForWritingComment = true;

    private LocalDateTime dateOfUpload;

    public Photo(List<String> captions, String photoName, List<Comment> comments, List<String> tags, boolean isFavorable, List<Album> albums, boolean permissionForWritingComment, LocalDateTime dateOfUpload) {
        this.captions = captions;
        this.photoName = photoName;
        this.comments = comments;
        this.tags = tags;
        this.isFavorable = isFavorable;
        this.albums = albums;
        this.permissionForWritingComment = permissionForWritingComment;
        this.dateOfUpload = dateOfUpload;
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

    public List<Album> getAlbums() {
        return albums;
    }

    public boolean isPermissionForWritingComment() {
        return permissionForWritingComment;
    }

    public LocalDateTime getDateOfUpload() {
        return dateOfUpload;
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

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
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

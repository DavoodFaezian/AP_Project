package DTO.Post;

import MainClasses.Post;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class PostDto {
    private String ownerId;
    private Set<String> photoIds= new LinkedHashSet<>();
    private Set<String> albumIds= new LinkedHashSet<>();
    private Set<String> commentIds = new LinkedHashSet<>();
    private Boolean commentsAllowed;
    private LocalDateTime createAt;
    private LocalDateTime lastModified;

    public PostDto(
            Post post
    ) {
        this.ownerId = post.getOwnerId();
        this.photoIds = post.getPhotoIds();
        this.albumIds = post.getAlbumIds();
        this.commentIds = post.getCommentIds();
        this.commentsAllowed = post.getCommentsAllowed();
        this.createAt = post.getCreateAt();
        this.lastModified = post.getLastModified();
    }


    public String getOwnerId() {
        return ownerId;
    }

    public Set<String> getPhotoIds() {
        return photoIds;
    }

    public Set<String> getAlbumIds() {
        return albumIds;
    }

    public Set<String> getCommentIds() {
        return commentIds;
    }

    public Boolean getCommentsAllowed() {
        return commentsAllowed;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }
}


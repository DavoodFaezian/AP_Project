package DTO.Post;

import java.util.Set;

public class EditPostDto {
    private String id;
    private String sessionId;
    private Set<String> photoIds;
    private Set<String> albumIds;
    private Boolean commentsAllowed;

    public EditPostDto(
            String id,
            String sessionId,
            Set<String> photoIds,
            Set<String> albumIds,
            Boolean commentsAllowed
    ) {
        this.id = id;
        this.sessionId = sessionId;
        this.photoIds = photoIds;
        this.albumIds = albumIds;
        this.commentsAllowed = commentsAllowed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Set<String> getPhotoIds() {
        return photoIds;
    }

    public void setPhotoIds(Set<String> photoIds) {
        this.photoIds = photoIds;
    }

    public Set<String> getAlbumIds() {
        return albumIds;
    }

    public void setAlbumIds(Set<String> albumIds) {
        this.albumIds = albumIds;
    }

    public Boolean getCommentsAllowed() {
        return commentsAllowed;
    }

    public void setCommentsAllowed(Boolean commentsAllowed) {
        this.commentsAllowed = commentsAllowed;
    }
}

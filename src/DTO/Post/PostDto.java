package DTO.Post;

import java.util.Objects;
import java.util.Set;

public class PostDto {
    private final String sessionId;
    private final String ownerId;
    private final Set<String> photoIds;
    private final Set<String> albumIds;
    private final Boolean commentsAllowed;

    public PostDto(
            String sessionId,
            String ownerId,
            Set<String> photoIds,
            Set<String> albumIds,
            Boolean commentsAllowed
    ) {
        this.sessionId = sessionId;
        this.ownerId = ownerId;
        this.photoIds = photoIds;
        this.albumIds = albumIds;
        this.commentsAllowed = commentsAllowed;
    }

    public String getSessionId() {
        return sessionId;
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

    public Boolean getCommentsAllowed() {
        return commentsAllowed;
    }


}


package DTO.Post;

import java.util.LinkedHashSet;
import java.util.Set;

public class AddPostDto {
    private final String sessionId;
    private Set<String> photoIds = new LinkedHashSet<>();
    private Set<String> albumIds = new LinkedHashSet<>();
    private final Boolean commentsAllowed;

    public AddPostDto(
            String sessionId,
            Set<String> photoIds,
            Set<String> albumIds,
            Boolean commentsAllowed
    ) {
        this.sessionId = sessionId;
        this.photoIds = photoIds;
        this.albumIds = albumIds;
        this.commentsAllowed = commentsAllowed;
    }

    public String getSessionId() {
        return sessionId;
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


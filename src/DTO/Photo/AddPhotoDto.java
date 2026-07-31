package DTO.Photo;

import java.util.Set;

public class AddPhotoDto {


    private String sessionId;
    private String ownerId;

    private String name;

    private Set<String> tags;

    private String caption;

    private Boolean isFavorable;

    private Boolean permissionForLeavingComment;

    public AddPhotoDto(String sessionId,String ownerId, String name, Set<String> tags, String caption, Boolean isFavorable, Boolean permissionForLeavingComment) {
        this.sessionId = sessionId;
        this.name = name;
        this.tags = tags;
        this.caption = caption;
        this.isFavorable = isFavorable;
        this.permissionForLeavingComment = permissionForLeavingComment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Boolean getFavorable() {
        return isFavorable;
    }

    public void setFavorable(Boolean favorable) {
        isFavorable = favorable;
    }

    public Boolean getPermissionForLeavingComment() {
        return permissionForLeavingComment;
    }

    public void setPermissionForLeavingComment(Boolean permissionForLeavingComment) {
        this.permissionForLeavingComment = permissionForLeavingComment;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}

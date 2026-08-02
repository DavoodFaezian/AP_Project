package DTO.Comment;

public class DeleteCommentDto {
    private String id;
    private String sessionId;
    private String postId;
    private String postOwnerId;

    public DeleteCommentDto(String id, String sessionId, String postId, String postOwnerId) {
        this.id = id;
        this.postId = postId;
        this.postOwnerId = postOwnerId;
        this.sessionId = sessionId;
    }

    public String getId() {
        return id;
    }

    public String getPostId() {
        return postId;
    }

    public String getPostOwnerId() {
        return postOwnerId;
    }

    public String getSessionId() {
        return sessionId;
    }

}

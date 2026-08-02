package DTO.Comment;

public class DeleteCommentDto {
    private String id;
    private String sessionId;
    private String commentOwnerId;
    private String postId;
    private String postOwnerId;

    public DeleteCommentDto(String id,String sessionId,String commentOwnerId, String postId, String postOwnerId) {
        this.id = id;
        this.postId = postId;
        this.postOwnerId = postOwnerId;
        this.sessionId = sessionId;
        this.commentOwnerId = commentOwnerId;
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

    public String getCommentOwnerId() {
        return commentOwnerId;
    }
}

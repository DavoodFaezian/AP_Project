package DTO.Comment;

public class AddCommentDto {
    private String ownerId;
    private String sessionId;
    private String script;
    private String postId;
    private String postOwnerId;

    public AddCommentDto(String ownerId,String sessionId, String script, String postId, String postOwnerId) {
        this.ownerId = ownerId;
        this.script = script;
        this.postId = postId;
        this.postOwnerId = postOwnerId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getScript() {
        return script;
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

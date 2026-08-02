package DTO.Comment;

public class AddCommentDto {
    private String sessionId;
    private String script;
    private String postId;
    private String postOwnerId;

    public AddCommentDto(String sessionId, String script, String postId, String postOwnerId) {
        this.sessionId = sessionId;
        this.script = script;
        this.postId = postId;
        this.postOwnerId = postOwnerId;
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

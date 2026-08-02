package DTO.Comment;

public class EditCommentDto {
    private String Id;
    private String sessionId;
    private String postId;
    private String script;

    public EditCommentDto(String id, String sessionId, String postId, String script) {
        Id = id;
        this.sessionId = sessionId;
        this.postId = postId;
        this.script = script;
    }

    public String getId() {
        return Id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getPostId() {
        return postId;
    }

    public String getScript() {
        return script;
    }
}

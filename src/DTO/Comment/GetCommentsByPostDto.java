package DTO.Comment;

public class GetCommentsByPostDto {
    private String postId;
    private String sessionId;

    public GetCommentsByPostDto(String sessionId,String postId) {
        this.postId = postId;
        this.sessionId = sessionId;
    }

    public String getPostId() {
        return postId;
    }

    public String getSessionId() {
        return sessionId;
    }
}

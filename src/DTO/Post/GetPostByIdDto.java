package DTO.Post;

public class GetPostByIdDto {
    private String postId;
    private String sessionId;
    private String ownerId;

    public GetPostByIdDto(String postId, String sessionId, String ownerId) {
        this.postId = postId;
        this.sessionId = sessionId;
        this.ownerId = ownerId;
    }

    public String getPostId() {
        return postId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getOwnerId() {
        return ownerId;
    }
}

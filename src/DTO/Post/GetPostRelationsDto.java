package DTO.Post;

public class GetPostRelationsDto {

    private final String sessionId;
    private final String postId;

    public GetPostRelationsDto(String sessionId, String postId) {
        this.sessionId = sessionId;
        this.postId = postId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getPostId() {
        return postId;
    }
}

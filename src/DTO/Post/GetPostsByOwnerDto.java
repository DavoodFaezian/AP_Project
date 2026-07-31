package DTO.Post;

public class GetPostsByOwnerDto {

    private final String sessionId;

    public GetPostsByOwnerDto(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }
}

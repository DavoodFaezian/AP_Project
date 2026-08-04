package DTO.User;

public class GetPostsByUserDto {
    private String sessionId;
    private String userId;

    public GetPostsByUserDto(String sessionId, String userId) {
        this.sessionId = sessionId;
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUserId() {
        return userId;
    }
}

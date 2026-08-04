package DTO.User;

public class GetUserByIdDto {
    String sessionId;
    String userId;

    public GetUserByIdDto(String sessionId, String userId) {
        this.sessionId = sessionId;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getSessionId() {
        return sessionId;
    }
}

package DTO.User;

public class SearchUserDto {
    private String sessionId;
    private String query;

    public SearchUserDto(String sessionId, String query) {
        this.sessionId = sessionId;
        this.query = query;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getQuery() {
        return query;
    }
}
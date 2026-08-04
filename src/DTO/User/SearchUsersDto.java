package DTO.User;

public class SearchUsersDto {

    private String sessionId;

    private String query;

    public SearchUsersDto(String sessionId, String query) {
        this.sessionId = sessionId;
        this.query = query;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
